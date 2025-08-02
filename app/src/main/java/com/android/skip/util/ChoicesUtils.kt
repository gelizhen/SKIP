package com.android.skip.util

import android.view.accessibility.AccessibilityNodeInfo
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import java.io.File

object ChoicesUtils {
    private val hashSet = HashSet<String>()

    private fun traverseNodeRecursively(
        node: AccessibilityNodeInfo?,
        action: (AccessibilityNodeInfo) -> Unit
    ) {
        if (node == null) return

        // 对当前节点执行操作
        action(node)

        // 遍历子节点
        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i)
            traverseNodeRecursively(childNode, action)
        }
    }

    fun generateChoices(rootNode: AccessibilityNodeInfo, filesDir: File) {
        traverseNodeRecursively(rootNode) { node ->
            val regex = Regex("\\d{6}")
            node.text?.toString()?.let { text ->
                regex.findAll(text).forEach { matchResult ->
                    hashSet.add(matchResult.value)

                    if (hashSet.size >= 880) {
                        val gson = Gson()
                        val jsonStr = gson.toJson(hashSet)

                        val file = File(filesDir, "choiceness.json")
                        file.writeText(jsonStr)
                    }
                    LogUtils.d("hashset size: ${hashSet.size}")
                }
            }
        }
    }
}