package com.ibroadlink.library.base.extend

import android.view.View
import androidx.databinding.BindingAdapter
import com.ibroadlink.library.base.R

/**
 * 在 xml 配置点击事件，可配置的属性如下：
 * onClickCommand 点击事件
 * isInterval 是否开启防止点击过快
 * intervalMilliseconds 防止点击过快的间隔时间，毫秒为单位
 *
 * 这里可全局设置是否开启防止点击事件过快的功能，局部可单独开启或关闭。
 *
 * 如果关闭，那么和 setOnClickListener 没啥区别
 */
var gIsClickInterval = true

/**
 * 点击事件时间间隔
 */
var gClickIntervalMilliseconds = 500

@BindingAdapter(
    value = ["onClickCommand", "isInterval", "intervalMilliseconds"],
    requireAll = false
)
fun onClickCommand(
    view: View,
    clickCommand: View.OnClickListener?,
    isInterval: Boolean?,
    intervalMilliseconds: Int?
) {
    var interval = isInterval
    // xml中没有配置，那么使用全局的配置
    if (interval == null) {
        interval = gIsClickInterval
    }
    // 没有配置时间，使用全局配置
    var milliseconds = intervalMilliseconds
    if (milliseconds == null) {
        milliseconds = gClickIntervalMilliseconds
    }
    if (interval) {
        clickCommand?.let { view.clickWithTrigger(milliseconds.toLong(), it) }
    } else {
        view.setOnClickListener(clickCommand)
    }
}

@BindingAdapter(
    value = ["clickFrequency", "multiClickCommand"],
    requireAll = false
)
fun multiClickCommand(
    view: View,
    frequency: Int?,
    clickCommand: View.OnClickListener?
) {
    if (clickCommand != null) {
        view.multiClickListener(frequency ?: 5, clickCommand)
    }
}

/**
 * get set
 * 给view添加一个上次触发时间的属性（用来屏蔽连击操作）
 */
private var <T : View>T.triggerLastTime: Long
    get() = if (getTag(R.id.base_triggerLastTimeKey) != null) getTag(R.id.base_triggerLastTimeKey) as Long else 0
    set(value) {
        setTag(R.id.base_triggerLastTimeKey, value)
    }

/**
 * get set
 * 给view添加一个延迟的属性（用来屏蔽连击操作）
 */
private var <T : View> T.triggerDelay: Long
    get() = if (getTag(R.id.base_triggerDelayKey) != null) getTag(R.id.base_triggerDelayKey) as Long else -1
    set(value) {
        setTag(R.id.base_triggerDelayKey, value)
    }

/**
 * 判断时间是否满足再次点击的要求（控制点击）
 */
private fun <T : View> T.clickEnable(): Boolean {
    var clickable = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        clickable = true
        triggerLastTime = currentClickTime
    }
    return clickable
}

/***
 * 带延迟过滤点击事件的 View 扩展
 * @param delay Long 延迟时间，默认500毫秒
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.clickWithTrigger(delay: Long = 500, block: View.OnClickListener) {
    triggerDelay = delay
    setOnClickListener {
        if (clickEnable()) {
            block.onClick(this)
        }
    }
}

/**
 * 连续点击达到点击次数后回调监听
 */
fun <T : View> T.multiClickListener(frequency: Int, block: View.OnClickListener) {
    val startIndex = 1
    val interval = 400
    setTag(R.id.base_multiClickFrequency, startIndex)
    setTag(R.id.base_multiClickLastTime, System.currentTimeMillis())

    setOnClickListener {
        var f = getTag(R.id.base_multiClickFrequency) as Int

        // 点击间隔超时，重置次数
        if (System.currentTimeMillis() - (getTag(R.id.base_multiClickLastTime) as Long) > interval) {
            setTag(R.id.base_multiClickFrequency, startIndex)
            f = startIndex
        }

        // 第一次点击，重置时间
        if (f == startIndex) {
            setTag(R.id.base_multiClickLastTime, System.currentTimeMillis())
        }

        if (f == frequency) {
            setTag(R.id.base_multiClickFrequency, startIndex)

            block.onClick(this)
        } else {
            val lastTime = getTag(R.id.base_multiClickLastTime) as Long
            if (System.currentTimeMillis() - lastTime < interval) {
                setTag(R.id.base_multiClickFrequency, f + 1)
            }
            setTag(R.id.base_multiClickLastTime, System.currentTimeMillis())
        }
    }
}