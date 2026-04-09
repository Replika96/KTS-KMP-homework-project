import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
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

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            //datastore
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.security.crypto.datastore.preferences)
            implementation(libs.androidx.security.crypto)

            //firebase
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:34.11.0"))

            implementation("com.google.firebase:firebase-crashlytics-ktx:18.6.1")
            implementation("com.google.firebase:firebase-analytics-ktx:21.6.1")
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.napier)
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

            // kvault
            implementation(libs.kvault)

            implementation(libs.ui.text)
            implementation(libs.ui.util)
            implementation(libs.richeditor.compose)

            //room
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.ktx)

            implementation(libs.androidx.navigation.compose)
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
        buildConfigField(
            "String",
            "STEPIK_CLIENT_ID",
            "\"${properties.getProperty("STEPIK_CLIENT_ID")}\""
        )
        buildConfigField(
            "String",
            "STEPIK_REDIRECT_URI",
            "\"${properties.getProperty("STEPIK_REDIRECT_URI")}\""
        )
    }

    signingConfigs {
        create("release") {
            storeFile = file("C:/Users/tazmi/OneDrive/Desktop/app/upload-keystore.jks")
            storePassword = System.getProperty("RELEASE_STORE_PASSWORD")
            keyAlias = System.getProperty("RELEASE_KEY_ALIAS")
            keyPassword = System.getProperty("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.firebase.crashlytics)
    add("kspAndroid", "androidx.room:room-compiler:2.8.4")
    add("kspIosArm64", "androidx.room:room-compiler:2.8.4")
    add("kspIosSimulatorArm64", "androidx.room:room-compiler:2.8.4")

    debugImplementation(libs.compose.uiTooling)
    debugImplementation(libs.leakcanary.android)
    debugImplementation(libs.napier.android.debug)

}
detekt {
    toolVersion = "1.23.8"
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = true
}
tasks.register<Detekt>("detektCompose") {
    description = "Run detekt on KMP sources"
    parallel = true
    ignoreFailures = false
    buildUponDefaultConfig = true
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))

    setSource(
        files(
            "src/commonMain/kotlin",
            "src/androidMain/kotlin",
            "src/iosMain/kotlin"
        )
    )

    reports {
        html.required.set(true)
        html.outputLocation.set(file("$rootDir/build/reports/detekt.html"))
        xml.required.set(true)
    }
}

ktlint {
    version.set("1.2.1")
    android.set(false)
    ignoreFailures.set(false)
    verbose.set(true)
    outputToConsole.set(true)
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}

