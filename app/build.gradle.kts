import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "me.amirkazemzade.netwidget"
    compileSdk = 36

    defaultConfig {
        applicationId = "me.amirkazemzade.netwidget"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildFeatures.buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Ensure to use a proper signing instead of this one to export the release APK
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    androidResources {
        @Suppress("UnstableApiUsage")
        generateLocaleConfig = true
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }

    androidComponents {
        onVariants { variant ->
            if (variant.name == "release") {
                val appName = "NetWidget" // Put your actual app name here
                val versionName = android.defaultConfig.versionName ?: "x.x.x"

                variant.outputs.forEach { output ->
                    // 1. Cast the output to an absolute implementation that contains outputFileName
                    if (output is com.android.build.api.variant.impl.VariantOutputImpl) {

                        // 2. Safely extract the ABI name
                        val abi = output.filters.find {
                            it.filterType == com.android.build.api.variant.FilterConfiguration.FilterType.ABI
                        }?.identifier ?: "universal"

                        // 3. Set the clean filename using the lazy Property .set() API
                        output.outputFileName.set("$appName-v$versionName-$abi.apk")
                    }
                }
            }
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget("11")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.compose)
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.android.view.material)

    implementation(libs.hilt.common)
    implementation(libs.hilt.work)
    implementation(libs.hilt.android)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    ksp(libs.hilt.android.compiler)

    implementation(libs.logging.interceptor)
    implementation(libs.square.retrofit)
    implementation(libs.square.retrofit.converter.ktx)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.appwidget.preview)
    implementation(libs.androidx.glance.preview)
    implementation(libs.androidx.glance.material3)

    implementation(libs.bumptech.glide)
    implementation(libs.valentinilk.shimmer)
    implementation(libs.whatif)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
}
