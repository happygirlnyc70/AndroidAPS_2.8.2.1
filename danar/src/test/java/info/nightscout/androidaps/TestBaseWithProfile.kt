package info.nightscout.androidaps

import android.content.Context
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import info.nightscout.androidaps.data.ProfileSealed
import info.nightscout.androidaps.extensions.pureProfileFromJson
import info.nightscout.androidaps.interfaces.ActivePlugin
import info.nightscout.androidaps.interfaces.Config
import info.nightscout.androidaps.interfaces.IobCobCalculator
import info.nightscout.androidaps.interfaces.ProfileFunction
import info.nightscout.androidaps.interfaces.ProfileStore
import info.nightscout.androidaps.plugins.bus.RxBus
import info.nightscout.androidaps.utils.DateUtil
import info.nightscout.androidaps.utils.FabricPrivacy
import info.nightscout.androidaps.utils.resources.ResourceHelper
import org.json.JSONObject
import org.junit.Before
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock

@Suppress("SpellCheckingInspection")
open class TestBaseWithProfile : TestBase() {

    @Mock lateinit var activePluginProvider: ActivePlugin
    @Mock lateinit var rh: ResourceHelper
    @Mock lateinit var iobCobCalculator: IobCobCalculator
    @Mock lateinit var fabricPrivacy: FabricPrivacy
    @Mock lateinit var profileFunction: ProfileFunction
    @Mock lateinit var config: Config
    @Mock lateinit var context: Context

    lateinit var dateUtil: DateUtil
    val rxBus = RxBus(aapsSchedulers, aapsLogger)

    val profileInjector = HasAndroidInjector { AndroidInjector { } }

    private lateinit var validProfileJSON: String
    lateinit var validProfile: ProfileSealed.Pure

    @Suppress("PropertyName") val TESTPROFILENAME = "someProfile"

    @Before
    fun prepareMock() {
        validProfileJSON = "{\"dia\":\"5\",\"carbratio\":[{\"time\":\"00:00\",\"value\":\"30\"}],\"carbs_hr\":\"20\",\"delay\":\"20\",\"sens\":[{\"time\":\"00:00\",\"value\":\"3\"}," +
            "{\"time\":\"2:00\",\"value\":\"3.4\"}],\"timezone\":\"UTC\",\"basal\":[{\"time\":\"00:00\",\"value\":\"1\"}],\"target_low\":[{\"time\":\"00:00\",\"value\":\"4.5\"}]," +
            "\"target_high\":[{\"time\":\"00:00\",\"value\":\"7\"}],\"startDate\":\"1970-01-01T00:00:00.000Z\",\"units\":\"mmol\"}"
        dateUtil = DateUtil(context)
        validProfile = ProfileSealed.Pure(pureProfileFromJson(JSONObject(validProfileJSON), dateUtil)!!)

        Mockito.doAnswer { invocation: InvocationOnMock ->
            val string = invocation.getArgument<Int>(0)
            val arg1 = invocation.getArgument<Int?>(1)
            String.format(rh.gs(string), arg1)
        }.`when`(rh).gs(anyInt(), anyInt())

        Mockito.doAnswer { invocation: InvocationOnMock ->
            val string = invocation.getArgument<Int>(0)
            val arg1 = invocation.getArgument<Double?>(1)
            String.format(rh.gs(string), arg1)
        }.`when`(rh).gs(anyInt(), anyDouble())

        Mockito.doAnswer { invocation: InvocationOnMock ->
            val string = invocation.getArgument<Int>(0)
            val arg1 = invocation.getArgument<String?>(1)
            String.format(rh.gs(string), arg1)
        }.`when`(rh).gs(anyInt(), anyString())

        Mockito.doAnswer { invocation: InvocationOnMock ->
            val string = invocation.getArgument<Int>(0)
            val arg1 = invocation.getArgument<String?>(1)
            val arg2 = invocation.getArgument<String?>(2)
            String.format(rh.gs(string), arg1, arg2)
        }.`when`(rh).gs(anyInt(), anyString(), anyString())

        Mockito.doAnswer { invocation: InvocationOnMock ->
            val string = invocation.getArgument<Int>(0)
            val arg1 = invocation.getArgument<String?>(1)
            val arg2 = invocation.getArgument<Int?>(2)
            String.format(rh.gs(string), arg1, arg2)
        }.`when`(rh).gs(anyInt(), anyString(), anyInt())

        Mockito.doAnswer { invocation: InvocationOnMock ->
            val string = invocation.getArgument<Int>(0)
            val arg1 = invocation.getArgument<Double?>(1)
            val arg2 = invocation.getArgument<String?>(2)
            String.format(rh.gs(string), arg1, arg2)
        }.`when`(rh).gs(anyInt(), anyDouble(), anyString())

        Mockito.doAnswer { invocation: InvocationOnMock ->
            val string = invocation.getArgument<Int>(0)
            val arg1 = invocation.getArgument<Double?>(1)
            val arg2 = invocation.getArgument<Int?>(2)
            String.format(rh.gs(string), arg1, arg2)
        }.`when`(rh).gs(anyInt(), anyDouble(), anyInt())

        Mockito.doAnswer { invocation: InvocationOnMock ->
            val string = invocation.getArgument<Int>(0)
            val arg1 = invocation.getArgument<Int?>(1)
            val arg2 = invocation.getArgument<Int?>(2)
            String.format(rh.gs(string), arg1, arg2)
        }.`when`(rh).gs(anyInt(), anyInt(), anyInt())

        Mockito.doAnswer { invocation: InvocationOnMock ->
            val string = invocation.getArgument<Int>(0)
            val arg1 = invocation.getArgument<Int?>(1)
            val arg2 = invocation.getArgument<String?>(2)
            String.format(rh.gs(string), arg1, arg2)
        }.`when`(rh).gs(anyInt(), anyInt(), anyString())

        Mockito.doAnswer { invocation: InvocationOnMock ->
            val string = invocation.getArgument<Int>(0)
            val arg1 = invocation.getArgument<Int?>(1)
            val arg2 = invocation.getArgument<Int?>(2)
            val arg3 = invocation.getArgument<String?>(3)
            String.format(rh.gs(string), arg1, arg2, arg3)
        }.`when`(rh).gs(anyInt(), anyInt(), anyInt(), anyString())

        Mockito.doAnswer { invocation: InvocationOnMock ->
            val string = invocation.getArgument<Int>(0)
            val arg1 = invocation.getArgument<Int?>(1)
            val arg2 = invocation.getArgument<String?>(2)
            val arg3 = invocation.getArgument<String?>(3)
            String.format(rh.gs(string), arg1, arg2, arg3)
        }.`when`(rh).gs(anyInt(), anyInt(), anyString(), anyString())

        Mockito.doAnswer { invocation: InvocationOnMock ->
            val string = invocation.getArgument<Int>(0)
            val arg1 = invocation.getArgument<Double?>(1)
            val arg2 = invocation.getArgument<Int?>(2)
            val arg3 = invocation.getArgument<String?>(3)
            String.format(rh.gs(string), arg1, arg2, arg3)
        }.`when`(rh).gs(anyInt(), anyDouble(), anyInt(), anyString())

        Mockito.doAnswer { invocation: InvocationOnMock ->
            val string = invocation.getArgument<Int>(0)
            val arg1 = invocation.getArgument<String?>(1)
            val arg2 = invocation.getArgument<Int?>(2)
            val arg3 = invocation.getArgument<String?>(3)
            String.format(rh.gs(string), arg1, arg2, arg3)
        }.`when`(rh).gs(anyInt(), anyString(), anyInt(), anyString())

    }

    fun getValidProfileStore(): ProfileStore {
        val json = JSONObject()
        val store = JSONObject()
        store.put(TESTPROFILENAME, JSONObject(validProfileJSON))
        json.put("defaultProfile", TESTPROFILENAME)
        json.put("store", store)
        return ProfileStore(profileInjector, json, dateUtil)
    }
}
