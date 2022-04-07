package info.nightscout.androidaps.interfaces

import info.nightscout.androidaps.plugins.general.autotune.data.ATProfile

interface Autotune {

    fun aapsAutotune()
    fun aapsAutotune(daysBack: Int, autoSwitch: Boolean, profileToTune: String = ""): String
    fun atLog(message: String)

    var pumpProfile: ATProfile?
    var tunedProfile: ATProfile?
    var result: String
    var selectedProfile: String
    var calculationRunning: Boolean
    var lastRun: Long
    var lastNbDays: String
    var copyButtonVisibility: Int
    var profileSwitchButtonVisibility: Int
    var lastRunSuccess: Boolean
    val autotuneStartHour: Int
        get() = 4
}