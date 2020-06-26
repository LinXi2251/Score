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
        myViewModel.scoreA.observe(this, Observer {
            textViewScoreA.text = it.toString()
        })

        myViewModel.scoreB.observe(this, Observer {
            textViewScoreB.text = it.toString()
        })
        val setButtonsEnable = setOf(
            buttonOK, button1A, button2A, button3A, button1B, button2B, button3B, imageButtonReset, imageButtonUndo
        )
        val setVisibleStart = setOf(
            buttonOK, editTextTextA, editTextTextB
        )
        val setVisibleEnd = setOf(
            textViewA, textViewB
        )
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
        editTextTextA.requestFocus()
        // 保留功能之后会用
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editTextTextA, 1)
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
        editTextTextA.addTextChangedListener(textWatcher)
        editTextTextB.addTextChangedListener(textWatcher)

        buttonOK.setOnClickListener { button ->
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

        imageButtonUndo.setOnClickListener {
            myViewModel.undo()
        }
        imageButtonReset.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.apply {
                setMessage("是否改变双方队名")
                setPositiveButton("是") { _, _ ->
                    myViewModel.setFlagValue(0)
                }
                setNegativeButton("否", null)
                create()
                show()
            }
            myViewModel.reset()
        }
    }

    override fun onStop() {
        super.onStop()
        myViewModel.saveData()
    }

}