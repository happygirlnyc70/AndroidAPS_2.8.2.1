package info.nightscout.androidaps.plugins.aps.openAPSSMB

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import dagger.android.support.DaggerFragment
import info.nightscout.androidaps.R
import info.nightscout.androidaps.databinding.OpenapsamaFragmentBinding
import info.nightscout.androidaps.interfaces.ActivePlugin
import info.nightscout.androidaps.plugins.aps.events.EventOpenAPSUpdateGui
import info.nightscout.androidaps.plugins.aps.events.EventOpenAPSUpdateResultGui
import info.nightscout.androidaps.plugins.bus.RxBus
import info.nightscout.androidaps.utils.DateUtil
import info.nightscout.androidaps.utils.FabricPrivacy
import info.nightscout.androidaps.utils.JSONFormatter
import info.nightscout.androidaps.utils.resources.ResourceHelper
import info.nightscout.androidaps.utils.rx.AapsSchedulers
import info.nightscout.shared.logging.AAPSLogger
import info.nightscout.shared.logging.LTag
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import org.json.JSONArray
import org.json.JSONException
import javax.inject.Inject

class OpenAPSSMBFragment : DaggerFragment() {

    private var disposable: CompositeDisposable = CompositeDisposable()

    @Inject lateinit var aapsLogger: AAPSLogger
    @Inject lateinit var aapsSchedulers: AapsSchedulers
    @Inject lateinit var rxBus: RxBus
    @Inject lateinit var rh: ResourceHelper
    @Inject lateinit var fabricPrivacy: FabricPrivacy
    @Inject lateinit var activePlugin: ActivePlugin
    @Inject lateinit var dateUtil: DateUtil
    @Inject lateinit var jsonFormatter: JSONFormatter
    private lateinit var refreshDialog: Runnable

    private val ID_MENU_RUN = 1

    private var _binding: OpenapsamaFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        _binding = OpenapsamaFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.swipeRefresh) {
            setColorSchemeColors(rh.gac(context, R.attr.colorPrimaryDark), rh.gac(context, R.attr.colorPrimary), rh.gac(context, R.attr.colorSecondary))
            setOnRefreshListener {
                binding.lastrun.text = rh.gs(info.nightscout.androidaps.R.string.executing)
                Thread { activePlugin.activeAPS.invoke("OpenAPSSMB swiperefresh", false) }.start()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.removeItem(ID_MENU_RUN)
        menu.add(Menu.FIRST, ID_MENU_RUN, 0, rh.gs(R.string.openapsma_run)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        menu.setGroupDividerEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            ID_MENU_RUN -> {
                binding.lastrun.text = rh.gs(R.string.executing)
                Thread { activePlugin.activeAPS.invoke("OpenAPSSMB menu", false) }.start()
                true
            }

            else        -> false
        }

    @Synchronized
    override fun onResume() {
        super.onResume()
        disposable += rxBus
            .toObservable(EventOpenAPSUpdateGui::class.java)
            .observeOn(aapsSchedulers.main)
            .subscribe({
                           updateGUI()
                       }, fabricPrivacy::logException)
        disposable += rxBus
            .toObservable(EventOpenAPSUpdateResultGui::class.java)
            .observeOn(aapsSchedulers.main)
            .subscribe({
                           updateResultGUI(it.text)
                       }, fabricPrivacy::logException)

        updateGUI()
    }

    @Synchronized
    override fun onPause() {
        super.onPause()
        disposable.clear()
    }

    @Synchronized
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Synchronized
    fun updateGUI() {
        if (_binding == null) return
        val openAPSSMBPlugin = activePlugin.activeAPS
        openAPSSMBPlugin.lastAPSResult?.let { lastAPSResult ->
            binding.result.text = jsonFormatter.format(lastAPSResult.json)
            binding.request.text = lastAPSResult.toSpanned()
        }
        openAPSSMBPlugin.lastDetermineBasalAdapter?.let { determineBasalAdapterSMBJS ->
            binding.glucosestatus.text = jsonFormatter.format(determineBasalAdapterSMBJS.glucoseStatusParam)
            binding.currenttemp.text = jsonFormatter.format(determineBasalAdapterSMBJS.currentTempParam)
            try {
                val iobArray = JSONArray(determineBasalAdapterSMBJS.iobDataParam)
                binding.iobdata.text = TextUtils.concat(rh.gs(R.string.array_of_elements, iobArray.length()) + "\n", jsonFormatter.format(iobArray.getString(0)))
            } catch (e: JSONException) {
                aapsLogger.error(LTag.APS, "Unhandled exception", e)
                @SuppressLint("SetTextI18n")
                binding.iobdata.text = "JSONException see log for details"
            }

            binding.profile.text = jsonFormatter.format(determineBasalAdapterSMBJS.profileParam)
            binding.mealdata.text = jsonFormatter.format(determineBasalAdapterSMBJS.mealDataParam)
            binding.scriptdebugdata.text = determineBasalAdapterSMBJS.scriptDebug.replace("\\s+".toRegex(), " ")
            openAPSSMBPlugin.lastAPSResult?.inputConstraints?.let {
                binding.constraints.text = it.getReasons(aapsLogger)
            }
        }
        if (openAPSSMBPlugin.lastAPSRun != 0L) {
            binding.lastrun.text = dateUtil.dateAndTimeString(openAPSSMBPlugin.lastAPSRun)
        }
        openAPSSMBPlugin.lastAutosensResult.let {
            binding.autosensdata.text = jsonFormatter.format(it.json())
        }
        binding.swipeRefresh.isRefreshing = false
    }

    @Synchronized
    private fun updateResultGUI(text: String) {
        if (_binding == null) return
        binding.result.text = text
        binding.glucosestatus.text = ""
        binding.currenttemp.text = ""
        binding.iobdata.text = ""
        binding.profile.text = ""
        binding.mealdata.text = ""
        binding.autosensdata.text = ""
        binding.scriptdebugdata.text = ""
        binding.request.text = ""
        binding.lastrun.text = ""
        binding.swipeRefresh.isRefreshing = false
    }
}
