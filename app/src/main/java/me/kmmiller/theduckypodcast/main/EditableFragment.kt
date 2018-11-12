package me.kmmiller.theduckypodcast.main

interface EditableFragment {
    fun onEdit()
    fun onSave()
    fun onCancel()
}