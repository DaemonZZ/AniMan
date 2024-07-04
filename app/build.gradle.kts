import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
val versionMajor = 1
val versionMinor = 3
val versionPatch = 6
val versionClassifier = null
val isSnapshot = true
val minimumSdkVersion = 31

android {
    namespace = "com.daemonz.animange"
    compileSdk = 34
    android.buildFeatures.buildConfig = true
    defaultConfig {
        applicationId = "com.daemonz.animange"
        minSdk = minimumSdkVersion
        targetSdk = 34
        versionCode = generateVersionCode()
        versionName = generateVersionName()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val projectProperties = readProperties(file("../local.properties"))
        buildConfigField("String", "BASE_URL", projectProperties["BASE_URL"] as String)
        buildConfigField("String", "IMG_BASE_URL", projectProperties["IMG_BASE_URL"] as String)
        buildConfigField("String", "TAG", projectProperties["TAG"] as String)
        buildConfigField("String", "STORAGE_PATH", projectProperties["STORAGE_PATH"] as String)
        buildConfigField("String", "SLUG_SECRET", projectProperties["SLUG_SECRET"] as String)
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            applicationVariants.all {
                val date = Date()
                val sdf = SimpleDateFormat("yyyyMMddHHmm")
                val formattedDate = sdf.format(date)
                outputs.all {
                    val output = this as? BaseVariantOutputImpl
                    output?.outputFileName =
                        "PhimFree_${buildType.name}_v${generateVersionName()}_${formattedDate}.apk"
                    setProperty("archivesBaseName", "PhimFree-v$versionName")
                }
            }
            val projectProperties = readProperties(file("../local.properties"))
            val admobAppId = projectProperties.getProperty("ADMOB_ID_PROD")
            manifestPlaceholders["admobAppId"] = admobAppId
        }
        debug {
            val projectProperties = readProperties(file("../local.properties"))
            val admobAppId = projectProperties.getProperty("ADMOB_ID_TEST")
            manifestPlaceholders["admobAppId"] = admobAppId
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    kapt {
        correctErrorTypes = true
        javacOptions {
            option("-Adagger.hilt.android.internal.disableAndroidSuperclassValidation=true")
        }
    }
}

fun readProperties(propertiesFile: File) = Properties().apply {
    propertiesFile.inputStream().use { fis ->
        load(fis)
    }
}
fun generateVersionCode(): Int {
    return minimumSdkVersion * 10000000 + versionMajor * 10000 + versionMinor * 100 + versionPatch
}

fun generateVersionName(): String {
    val versionName = "${versionMajor}.${versionMinor}.${versionPatch}"
//    if (ext.versionClassifier == null && ext.isSnapshot) {
//        ext.versionClassifier = "SNAPSHOT"
//    }
//
//    if (ext.versionClassifier != null) {
//        versionName += "-" + ext.versionClassifier
//    }
    return versionName;
}
dependencies {
//    kapt(libs.artifactid)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Kotlin nav
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Feature module Support
    implementation(libs.androidx.navigation.dynamic.features.fragment)
    kapt (libs.androidx.lifecycle.compiler)

    //Coroutine
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)

    // Glide
    implementation (libs.glide)
    annotationProcessor (libs.compiler)

    //Room
    implementation (libs.androidx.room.runtime)
    annotationProcessor (libs.androidx.room.compiler)
    implementation (libs.androidx.room.rxjava3)
    implementation(libs.androidx.room.paging)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)

    //Lifecycle
    // ViewModel
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    // LiveData
    implementation (libs.androidx.lifecycle.livedata.ktx)
    implementation (libs.androidx.lifecycle.extensions)
    implementation (libs.androidx.lifecycle.runtime.ktx)
    implementation (libs.androidx.fragment.ktx)
    // Annotation processor

    // Retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.rxjava3.retrofit.adapter)

    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

//HILT
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.fragment)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.swiperefreshlayout)
    //FireBase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.ui.auth)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.ui.storage)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.messaging.directboot)

    //Facebook
//    implementation (libs.facebook.android.sdk)

//    implementation(libs.androidx.credentials)
//    implementation(libs.androidx.credentials.play.services.auth)
    //Room
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)

    // To use Kotlin annotation processing tool (kapt)
    kapt(libs.androidx.room.compiler)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
    //admobs
    implementation(libs.play.services.ads)
    implementation(libs.androidx.core.splashscreen)
    //flexbox
    implementation(libs.flexbox)
    implementation(libs.feature.delivery)
}