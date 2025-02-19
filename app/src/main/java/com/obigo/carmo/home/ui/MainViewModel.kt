package com.obigo.carmo.home.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import com.obigo.carmo.home.R
import com.obigo.carmo.home.animation.AnimationFunctions
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")

    private val context = getApplication<Application>().applicationContext

    /**
     * 최신 버전 LiveData
     */
    private val _recentVersion = MutableLiveData<String>()
    val recentVersion: LiveData<String> get() = _recentVersion

    /**
     * 현재 정류장 LiveData
     */
    private val _currentStation = MutableLiveData<String>()
    val currentStation : LiveData<String> get() = _currentStation

    /**
     * 현재 온도 LiveData
     */
    private val _currentTemperature = MutableLiveData<Float>()
    val currentTemperature : LiveData<Float> = _currentTemperature

    /**
     * 타이머
     */
    private var timer = Timer()

    /**
     * 정류장
     */
    private val station = ArrayList<String>()

    /**
     * 애니메이션 가동 시간
     */
     var animationPeriod : Long = 3000

    /**
     * 애니메이션 함수 클래스
     */
    val animationFunctions = AnimationFunctions(context, this)

    /**
     * 현재 선택된 애니메이션
     */
    var selectedAnimation : Int = 0

    /**
     * 업데이트 시 다운로드 받을 URL
     */
    var updateUrl: String = ""

    /**
     * 최신 버전
     */
    var latestVersion : String = ""

    /**
     * 이동할 앱의 패키지 명
     */
    val componentName: ComponentName = ComponentName("com.obigo.carmoupdater", "com.obigo.carmoupdater.MainActivity")

    init {
        _currentTemperature.value = 20.0F
    }

    /**
     * AppUpdater 라이브러리의 유틸 클래스
     */
    val appUpdaterUtils: AppUpdaterUtils = AppUpdaterUtils(context)
        .setUpdateFrom(UpdateFrom.GITHUB)
        .setGitHubUserAndRepo("mondseo", "obigo-carmo")
        .withListener(object : AppUpdaterUtils.UpdateListener {
            override fun onSuccess(update: Update?, isUpdateAvailable: Boolean?) {
                Log.d("Latest Version", update?.latestVersion.toString())
                _recentVersion.value = "최신 버전 : ${update?.latestVersion}"
                updateUrl = "https://github.com/MondSeo/obigo-carmo/releases/download/v${update?.latestVersion}/Carmo_v${update?.latestVersion}.apk"
                latestVersion = update?.latestVersion.toString()
                Log.d("Latest Version", updateUrl)
            }

            override fun onFailed(error: AppUpdaterError?) {
                _recentVersion.value = context.getString(R.string.defaultRecentVersion)
                Log.d(TAG, "$error");
            }
        })

    /**
     * 가공 데이터
     */
    fun initStationData(){
        station.apply {
            station.add("버스 정류장1")
            station.add("버스 정류장2")
            station.add("버스 정류장3")
            station.add("버스 정류장4")
            station.add("버스 정류장5")
            station.add("버스 정류장6")
            station.add("버스 정류장7")
            station.add("버스 정류장8")
        }
    }

    /**
     * 정류장 이동
     */
    fun stationMoving(view: View){
        var i = 0
        timer.cancel()
        val timerTask = object : TimerTask(){
            override fun run() {
                try {
                    _currentStation.postValue(station[i])
                } catch(e : IndexOutOfBoundsException){
                    i = 0
                    _currentStation.postValue(station[i])
                }
                animationFunctions.dropDownAnimationChanged(view, selectedAnimation)
                i++
            }
        }
        timer = Timer()
        timer.schedule(timerTask,0, animationPeriod)
    }

    fun carmoWindowMoving(view: View){
        var i = 0
        timer.cancel()
        val timerTask = object : TimerTask(){
            override fun run() {
                animationFunctions.dropDownAnimationChanged(view, selectedAnimation)
                i++
            }
        }
        timer = Timer()
        timer.schedule(timerTask,0, animationPeriod)
    }
}