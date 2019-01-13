package hu.attilavegh.vbkoveto.dummy

import java.util.ArrayList
import java.util.HashMap

object DummyContent {

    val ITEMS: MutableList<DummyItem> = ArrayList()

    private val COUNT = 5

    init {
        for (i in 1..COUNT) {
            addItem(createDummyItem(i))
        }
    }

    private fun addItem(item: DummyItem) {
        ITEMS.add(item)
    }

    private fun createDummyItem(position: Int): DummyItem {
        return DummyItem(position.toString(), "Járat")
    }

    data class DummyItem(val id: String, val content: String) {
        override fun toString(): String = "$content $id"
    }
}
