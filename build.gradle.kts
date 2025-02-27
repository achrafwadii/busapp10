
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
    alias(libs.plugins.android.application) apply false
    id("org.jetbrains.compose") version "1.6.10"
    id ("org.jetbrains.kotlin.android") version "2.1.0" apply false
    alias(libs.plugins.kotlin.compose) apply false



}


