package com.example.mpaz_pro_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mpaz_pro_2.ui.theme.MPAZ_PRO_2Theme
import com.example.mpaz_pro_2.ui.AppNavigation
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MPAZ_PRO_2Theme {
                AppNavigation()
            }
        }
    }
}