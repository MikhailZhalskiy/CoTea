package com.mw.cotea.main

sealed interface MainCommand {
    object LoadOne: MainCommand
    object LoadTwo: MainCommand
}