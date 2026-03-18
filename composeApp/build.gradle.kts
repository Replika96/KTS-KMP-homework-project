import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)

        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.napier)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.okio)
            // coil
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.koin.androidx.compose)
            implementation(libs.koin.compose.viewmodel)
            //koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.material3.v190)
            implementation(libs.material.icons.extended)
            // ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.auth)

            // serialization
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.kotlinx.serialization.json)

            // logging
            implementation(libs.ktor.client.logging)
            //oauth
            implementation(libs.appauth.kotlin)
            //datastore
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.security.crypto.datastore.preferences)
            implementation(libs.androidx.security.crypto)
            //room
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.ktx)
            // kvault
            implementation("com.liftric:kvault:1.12.0")
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())
android {
    namespace = "org.kts.tazmin"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.kts.tazmin"
        minSdk = libs.versions.android.minSdk.get().toInt()
        manifestPlaceholders["appAuthRedirectScheme"] = "stepik-cli"
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "STEPIK_CLIENT_ID", "\"${properties.getProperty("STEPIK_CLIENT_ID")}\"")
        buildConfigField("String", "STEPIK_REDIRECT_URI", "\"${properties.getProperty("STEPIK_REDIRECT_URI")}\"")
    }
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    add("kspAndroid", "androidx.room:room-compiler:2.8.4")
    add("kspIosArm64", "androidx.room:room-compiler:2.8.4")
    add("kspIosSimulatorArm64", "androidx.room:room-compiler:2.8.4")

    debugImplementation(libs.compose.uiTooling)
}
