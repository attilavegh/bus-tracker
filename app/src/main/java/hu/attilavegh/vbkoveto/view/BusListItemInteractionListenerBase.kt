package hu.attilavegh.vbkoveto.view

import hu.attilavegh.vbkoveto.model.Bus

interface BusListItemInteractionListenerBase {
    fun onBusSelection(bus: Bus)
}