package com.example.score

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

const val VALUES = "values"
const val SCORE_A = "score_A"
const val SCORE_B = "score_B"
const val VISIBLE_FLAG = "visible_flag"
const val TEAM_NAME_A = "team_name_A"
const val TEAM_NAME_B = "team_name_B"
class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val shp = application.getSharedPreferences(VALUES, Context.MODE_PRIVATE)

    private val _scoreA = MutableLiveData<Int>().also {
        it.value = shp.getInt(SCORE_A, 0)
    }
    private val _scoreB = MutableLiveData<Int>().also {
        it.value = shp.getInt(SCORE_B, 0)
    }
    private val _visibleFlag = MutableLiveData<Int>().also {
        it.value = shp.getInt(VISIBLE_FLAG, 0)
    }
    private val _teamNameA = MutableLiveData<String>().also {
        it.value = shp.getString(TEAM_NAME_A, application.getString(R.string.textView_A))
    }
    private val _teamNameB = MutableLiveData<String>().also {
        it.value = shp.getString(TEAM_NAME_B, application.getString(R.string.textView_B))
    }
    val scoreA: LiveData<Int> = _scoreA
    val scoreB: LiveData<Int> = _scoreB
    val teamNameA:LiveData<String> = _teamNameA
    val teamNameB:LiveData<String> = _teamNameB
    val visibleFlag: LiveData<Int> = _visibleFlag

    private val listA = Stack<Int>()
    private val listB = Stack<Int>()

    fun setFlagValue(flag:Int) {
        _visibleFlag.value = flag
    }

    fun setTeamNameA(name:String) {
        _teamNameA.value = "$name 队"
    }

    fun setTeamNameB(name:String) {
        _teamNameB.value = "$name 队"
    }

    fun addA(x:Int) {
        listA.add(_scoreA.value)
        listB.add(_scoreB.value)
        _scoreA.value = _scoreA.value?.plus(x)
    }

    fun addB(x:Int) {
        listA.add(_scoreA.value)
        listB.add(_scoreB.value)
        _scoreB.value = _scoreB.value?.plus(x)
    }

    fun undo() {
        if (!(listA.isEmpty())) {
            _scoreA.value = listA.pop()
        }
        if (!(listB.isEmpty())) {
            _scoreB.value = listB.pop()
        }
    }

    fun reset() {
        listA.clear()
        listB.clear()
        _scoreA.value = 0
        _scoreB.value = 0
    }

    fun saveData() {
        shp.edit().apply {
            putInt(SCORE_A, _scoreA.value!!)
            putInt(SCORE_B, _scoreB.value!!)
            putInt(VISIBLE_FLAG, _visibleFlag.value!!)
            putString(TEAM_NAME_A, _teamNameA.value)
            putString(TEAM_NAME_B, _teamNameB.value)
        }.apply()
    }

}