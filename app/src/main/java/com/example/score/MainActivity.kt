package com.example.score

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val myViewModel by viewModels<MyViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 设置组件的集合实现其是否可见
        val setButtonsEnable = setOf(
            buttonOK, button1A, button2A, button3A, button1B, button2B, button3B, imageButtonReset, imageButtonUndo
        )
        val setVisibleStart = setOf(
            buttonOK, editTextTextA, editTextTextB
        )
        val setVisibleEnd = setOf(
            textViewA, textViewB
        )
        // end

        // 数据展示用Observer进行观察
        myViewModel.scoreA.observe(this, Observer {
            textViewScoreA.text = it.toString()
        })

        myViewModel.scoreB.observe(this, Observer {
            textViewScoreB.text = it.toString()
        })
        // 根据flag的状态判断是在计数状态还是在设置队名状态
        myViewModel.visibleFlag.observe(this, Observer { flag ->
            // flag == 1 --> 已经完成队名的初始化 --> 开始界面部分组件隐藏
            if (flag == 1) {
                setVisibleStart.forEach {
                    it.visibility = View.GONE
                }
                setVisibleEnd.forEach {
                    it.visibility = View.VISIBLE
                }
                setButtonsEnable.forEach {
                    it.isEnabled = true
                }
            } else {
                setVisibleStart.forEach {
                    it.visibility = View.VISIBLE
                }
                setVisibleEnd.forEach {
                    it.visibility = View.GONE
                }
                setButtonsEnable.forEach {
                    it.isEnabled = false
                }
            }
        })

        myViewModel.teamNameA.observe(this, Observer {
            textViewA.text = it
        })

        myViewModel.teamNameB.observe(this, Observer {
            textViewB.text = it
        })
        // end

        editTextTextA.requestFocus()
        // 保留功能之后会用到对键盘的一个操作
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editTextTextA, 1)
        // 监视文本框 --> 当两个都有值的时候使得按键可用
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val strA = editTextTextA.text.toString().trim()
                val strB = editTextTextB.text.toString().trim()
                buttonOK.isEnabled = (strA.isNotEmpty() && strB.isNotEmpty())
            }

        }
        // 添加监视
        editTextTextA.addTextChangedListener(textWatcher)
        editTextTextB.addTextChangedListener(textWatcher)
        // end

        // 按键触发设置双方队名
        buttonOK.setOnClickListener { button ->
            // 隐藏键盘
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).also {
                it.hideSoftInputFromWindow(button.windowToken, 0)
            }
            try {
                myViewModel.setFlagValue(1)
                myViewModel.setTeamNameA(editTextTextA.text.toString())
                myViewModel.setTeamNameB(editTextTextB.text.toString())
                editTextTextA.text = null
                editTextTextB.text = null
            } catch (e: Exception) {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.apply {
                    setMessage("输入不合法")
                    setPositiveButton("确定", null)
                    create()
                    show()
                }

            }
        }
        // end

        // 计数
        button1A.setOnClickListener {
            myViewModel.addA(1)
        }
        button2A.setOnClickListener {
            myViewModel.addA(2)
        }
        button3A.setOnClickListener {
            myViewModel.addA(3)
        }

        button1B.setOnClickListener {
            myViewModel.addB(1)
        }
        button2B.setOnClickListener {
            myViewModel.addB(2)
        }
        button3B.setOnClickListener {
            myViewModel.addB(3)
        }
        // end

        imageButtonUndo.setOnClickListener {
            myViewModel.undo()
        }
        imageButtonReset.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.apply {
                setMessage("是否改变双方队名")
                setPositiveButton("是") { _, _ ->
                    myViewModel.setFlagValue(0) // flag = 0 状态为修改队名状态
                }
                setNegativeButton("否", null)
                create()
                show()
            }
            myViewModel.reset()
        }
    }
    // 保存数据
    override fun onStop() {
        super.onStop()
        myViewModel.saveData()
    }

}