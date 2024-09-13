@file:Suppress("UNUSED_EXPRESSION")

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	alias(libs.plugins.androidApplication)
	groovy
	id("androidx.benchmark")
	id("androidx.navigation.safeargs.kotlin")
	id("com.android.application") version "8.4.0" apply false
	id("com.android.dynamic-feature")
	id("com.android.library") version "4.4.1" apply false
	id("com.google.firebase.crashlytics")
	id("com.google.gms.google-services") version "4.4.0" apply false
	id("dagger.hilt.android.plugin")
	id("io.spring.dependency-management") version "1.1.6"
	id("java")
	id("kotlin-android")
	id("kotlin-kapt")
	id("org.jetbrains.kotlin.android") version "1.6.10" apply false
	id("org.springframework.boot") version "3.3.2-SNAPSHOT"
	kotlin("android")
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
	war
}

android {
	buildFeatures {
		compose = true
		viewBinding = true
	}
	buildToolsVersion build_tools_version
	buildTypes {
		debug {
			// Since debuggable can't be modified by gradle for library modules,
			// it must be done in a manifest - see src/androidTest/AndroidManifest.xml
			minifyEnabled true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"benchmark-proguard-rules.pro"
			)
		}
		release {
			isDebuggable = false
			isDefault = true
			isMinifyEnabled = true
			isShrinkResources = true
			minifyEnabled = false
			shrinkResources = true
			signingConfig signingConfigs.debug
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		coreLibraryDesugaringEnabled true

		kotlinCompilerExtensionVersion = "1.4.3"

		sourceCompatibility = 1.8
		sourceCompatibility = JavaVersion.VERSION_1_8
		sourceCompatibility = JavaVersion.VERSION_17

		targetCompatibility = 1.8
		targetCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_17
	}
	compileSdk = 33
	compileSdkVersion = 33
	composeOptions {
		kotlinCompilerExtensionVersion compose_version
		kotlinCompilerVersion = 1.8.20
	}
	defaultConfig {
		applicationId = "com.example.app"
		consumerProguardFiles "consumer-rules.pro"
		javaCompileOptions {
			annotationProcessorOptions {
				arguments += mapOf(
					"room.schemaLocation" to "$projectDir/schemas",
					// Enable Room's incremental annotation processor
					"room.incremental" to "true"
				)
			}
		}
		manifestPlaceholders["auth0Domain"] = "@string/com_auth0_domain"
		manifestPlaceholders["auth0Scheme"] = "@string/com_auth0_scheme"
		minSdk = 28
		minSdkVersion = 28
		multiDexEnabled = true
		targetSdk = 33
		targetSdkVersion = 33
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables.useSupportLibrary = true
		vectorDrawables {
			useSupportLibrary = true
		}
		versionCode = 1
		versionName = "1.0.0-alpha01"
	}
	kotlinOptions {
		jvmTarget = "1.8"
		jvmTarget = JavaVersion.VERSION_1_8.toString()
		jvmTarget = JavaVersion.VERSION_17.majorVersion
		allWarningsAsErrors = true
		freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
		useIR = true
	}
	lint {
		baseline = file("lint-baseline.xml")
	}
	lintOptions {
		lintConfig file("../lint.xml")
		warningsAsErrors false
	}
	namespace = "com.example.app"
	ndkVersion = "25.2.9519653"
	signingConfigs {
		release {
			storeFile file("release.keystore")
			keyAlias "release_key"
			keyPassword "password"
			v1SigningEnabled true
			v2SigningEnabled true
		}
	}
	testBuildType = "release"
	testOptions {
		devices {
			squareApi30(com.android.build.api.dsl.ManagedVirtualDevice) {
				device = "Wear OS Square"
				apiLevel = 30
				systemImageSource = "android-wear"
			}
			roundApi28(com.android.build.api.dsl.ManagedVirtualDevice) {
				device = "Wear OS Large Round"
				apiLevel = 28
				systemImageSource = "android-wear"
			}
			roundApi30(com.android.build.api.dsl.ManagedVirtualDevice) {
				device = "Wear OS Large Round"
				apiLevel = 30
				systemImageSource = "android-wear"
			}
			roundApi33(com.android.build.api.dsl.ManagedVirtualDevice) {
				device = "Wear OS Large Round"
				apiLevel = 33
				systemImageSource = "android-wear"
			}
		}
		unitTests {
			includeAndroidResources = true
		}
		unitTests.all {
			systemProperty "robolectric.enabledSdks", "28"
			systemProperty "robolectric.enabledSdks", "30"
		}
	}
	// Workaround for https://github.com/Kotlin/kotlinx.coroutines/issues/2023
	packagingOptions {
		// for JNA and JNA-platform
		exclude "META-INF/AL2.0"
		exclude "META-INF/LGPL2.1"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

dependencies {

	val activity_version = "1.7.0"
	val room_version = "2.5.0"

	constraints {
		implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0") {
			because("kotlin-stdlib-jdk7 is now a part of kotlin-stdlib")
		}
		implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0") {
			because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
		}
	}

	androidTestImplementation("androidx.benchmark:benchmark-junit4:1.2.2")
	androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_version")
	androidTestImplementation("androidx.test:core-ktx:$androidx_test_core_version")
	androidTestImplementation("androidx.test.espresso:espresso-accessibility:$androidx_test_espresso_version")
	androidTestImplementation("androidx.test.espresso:espresso-core:$androidx_test_espresso_version")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
	androidTestImplementation("androidx.test.ext:junit:$androidx_test_ext_junit_version")
	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.ext:junit-ktx:$androidx_test_ext_junit_version")
	androidTestImplementation("androidx.test:rules:$androidx_test_rules_version")
	androidTestImplementation("androidx.test:runner:$androidx_test_runner_version")
	androidTestImplementation("com.google.truth:truth:$truth_version")
	androidTestImplementation("junit:junit:$junit_version")
	androidTestImplementation("libs.androidx.test.ext.junit")
	androidTestImplementation("libs.com.google.truth")
	androidTestImplementation("libs.espresso.core")
	androidTestImplementation("libs.kotlinx.coroutines.test")
	androidTestImplementation("libs.test.core")
	androidTestImplementation("libs.test.core.ktx")
	androidTestImplementation("libs.test.espresso.core")
	androidTestImplementation("libs.test.ext.junit")
	androidTestImplementation("libs.test.ext.junit.ktx")
	androidTestImplementation("libs.test.ext.truth")
	androidTestImplementation("libs.test.rules")
	androidTestImplementation("libs.test.runner")
	androidTestImplementation("libs.test.uiautomator")
	androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))

	annotationProcessor("androidx.room:room-compiler:$room_version")
	annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")

	api("androidx.room:room-ktx:$room_version")

	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:$desugar_jdk_version")

	// We need this for tests. It should be 'debugImplementation' to put the test manifest in the
	// app package.
	debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_version")
	debugImplementation("androidx.compose.ui:ui-tooling")

	implementation("androidx.activity:activity:$activity_version")
	implementation("androidx.activity:activity-compose:$activity_version")
	implementation("androidx.activity:activity-ktx:$activity_version")
	implementation("androidx.annotation:annotation:1.7.0")
	implementation("androidx.appcompat:appcompat:$appcompat_version")
	implementation("androidx.biometric:biometric:1.2.0-alpha05")
	implementation("androidx.cardview:cardview:$cardview_version")
	implementation("androidx.compose.material3:material3")
	implementation("androidx.compose.material:material:$compose_version")
	implementation("androidx.compose.material:material-icons-extended:$compose_version")
	implementation("androidx.compose.ui:ui:$compose_version")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling:$compose_version")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.constraintlayout:constraintlayout:$constraintlayout_version")
	implementation("androidx.core:core-ktx:$core_version")
	implementation("androidx.core:core-ktx:1.12.0")
	implementation("androidx.fragment:fragment:1.3.2")
	implementation("androidx.fragment:fragment-ktx:$fragment_ktx_version")
	implementation("androidx.hilt:hilt-navigation-compose:1.0.0-alpha03")
	implementation("androidx.hilt:hilt-navigation-fragment:$hilt_navigation_fragment_version")
	implementation("androidx.legacy:legacy-support-v4:$legacy_support_v4_version")
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
	implementation("androidx.multidex:multidex:2.0.1")
	implementation("androidx.navigation:navigation-compose:$nav_compose_version")
	implementation("androidx.navigation:navigation-fragment:2.5.3")
	implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
	implementation("androidx.navigation:navigation-ui:2.7.4")
	implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
	implementation("androidx.preference:preference:1.1.0")
	implementation("androidx.preference:preference-ktx:$preference_version")
	implementation("androidx.recyclerview:recyclerview:$recyclerview_version")
	implementation("androidx.recyclerview:recyclerview-selection:$recyclerview_selection_version")
	implementation("androidx.room:room-runtime:$room_version")
	implementation("androidx.slidingpanelayout:slidingpanelayout:$sliding_pane_layout_version")
	implementation("androidx.webkit:webkit:1.4.0")
	implementation("androidx.work:work-runtime:2.8.1")
	implementation("com.airbnb.android:lottie:6.1.0")
	implementation("com.android.car.ui:car-ui-lib:2.5.1")
	implementation("com.android.support:multidex:$multidex_library_version")
	implementation("com.android.volley:volley:1.2.1")
	implementation("com.auth0.android:auth0:2.+")
	implementation("com.facebook.fresco:fresco:2.5.0")
	implementation("com.firebaseui:firebase-ui-auth:7.2.0")
	implementation("com.firebaseui:firebase-ui-firestore:8.0.2")
	implementation("com.github.bumptech.glide:glide:4.16.0")
	implementation("com.github.f0ris.sweetalert:library:1.5.6")
	implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
	implementation("com.google.accompanist:accompanist-flowlayout:$accompanist_version")
	implementation("com.google.accompanist:accompanist-insets-ui:$accompanist_version")
	implementation("com.google.android.exoplayer:exoplayer:2.19.1")
	implementation("com.google.android.exoplayer:extension-mediasession:2.19.1")
	implementation("com.google.android.gms:play-services-auth:20.7.0")
	implementation("com.google.android.gms:play-services-location:21.0.1")
	implementation("com.google.android.gms:play-services-maps:18.2.0")
	implementation("com.google.android.material:material:$material_version")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("com.google.dagger:hilt-android:$hilt_version")
	implementation("com.google.firebase:firebase-analytics:21.5.0")
	implementation("com.google.firebase:firebase-analytics-ktx")
	implementation("com.google.firebase:firebase-auth")
	implementation("com.google.firebase:firebase-auth:22.2.0")
	implementation("com.google.firebase:firebase-crashlytics:18.5.1")
	implementation("com.google.firebase:firebase-database:20.3.0")
	implementation("com.google.firebase:firebase-database-ktx:20.3.0")
	implementation("com.google.firebase:firebase-firestore:24.8.1")
	implementation("com.google.firebase:firebase-inappmessaging:20.3.5")
	implementation("com.google.firebase:firebase-messaging:23.2.1")
	implementation("com.google.firebase:firebase-storage:20.3.0")
	implementation("com.google.zxing:core:3.5.0")
	implementation("com.intuit.sdp:sdp-android:1.0.6")
	implementation("com.journeyapps:zxing-android-embedded:4.2.0")
	implementation("com.squareup:android-times-square:1.6.5@aar")
	implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
	implementation("com.squareup.picasso:picasso:2.71828")
	implementation("com.squareup.retrofit2:converter-gson:2.9.0")
	implementation("com.squareup.retrofit2:retrofit:2.9.0")
	implementation("de.hdodenhof:circleimageview:3.1.0")
	implementation(fileTree(dir: "libs", include: ["*.jar"])
	implementation("libs.androidx.activity.ktx")
	implementation("libs.androidx.core.ktx")
	implementation("libs.androidx.wear")
	implementation("libs.appcompat")
	implementation("libs.kotlinx.coroutines.android")
	implementation("libs.material")
	implementation("net.bytebuddy:byte-buddy:$byte_buddy_version")
	implementation("org.apache.groovy:groovy")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
	implementation("org.mindrot:jbcrypt:0.4")
	implementation("org.openai:openai-api-java:1.0.0")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation(platform("androidx.compose:compose-bom:2023.03.00"))
	implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
	implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
	implementation(project(":app")
	implementation(project(":shared")

	kapt("androidx.hilt:hilt-compiler:$hilt_compiler_version")
	kapt("androidx.room:room-compiler:$room_version")
	kapt("com.google.dagger:hilt-android-compiler:$hilt_version")

	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

	testImplementation("androidx.arch.core:core-testing:$core_testing_version")
	testimplementation("androidx.room:room-testing:$room_version")
	testImplementation("androidx.test:core:$androidx_test_core_version")
	testimplementation("androidx.test.ext:junit:$androidx_test_ext_junit_version")
	testImplementation("com.google.truth:truth:$truth_version")
	testimplementation("io.mockk:mockk:$mockk_version")
	testImplementation("junit:junit:$junit_version")
	testimplementation("libs.junit")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testimplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")
	testImplementation("org.mockito:mockito-core:3.12.4")
	testimplementation("org.robolectric:robolectric:$robolectric_version")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

configurations.implementation {
	exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
}

allprojects {
	repositories {
		google()
		mavenCentral()
		maven { // repo for TFLite snapshot
			name "ossrh-snapshot"
			url "https://oss.sonatype.org/content/repositories/snapshots"
		}
	}
	tasks.withType<JavaCompile> {
		options.compilerArgs.add("-Xlint:deprecation")
		options.compilerArgs.add("-Xlint:unchecked")
	}
}

buildscript {
	val agp_version by extra("8.1.1")
	dependencies {
		classpath("com.android.tools.build:gradle:$agp_version")
		classpath("com.diffplug.spotless:spotless-plugin-gradle:5.11.1")
		classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
		classpath("com.google.gms:google-services:4.4.0")
		classpath("de.undercouch:gradle-download-task:5.5.0")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20")
		// NOTE: Do not place your application dependencies here; they belong
		// in the individual module build.gradle files
	}
	ext {
		// Top-level variables used for versioning
	}
	repositories {
		google()
		mavenCentral()
	}
}

group = "com.example"

java {
	sourceCompatibility = "22"
	toolchain {
		languageVersion = JavaLanguageVersion.of(22)
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
}

task clean(type: Delete) {
	delete rootProject.buildDir
}
tasks.register("clean", Delete::class) {
	delete(rootProject.buildDir)
}
tasks.register<Wrapper>("wrapper") {
    gradleVersion = "5.6.4"
}
tasks.register("prepareKotlinBuildScriptModel") {}
tasks.named("test") {
	useJUnitPlatform()
}
tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).all {
	kotlinOptions {
		freeCompilerArgs += [
			"-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
		]
	}
}
tasks.withType<Test> {
	useJUnitPlatform()
}

subprojects {
	apply plugin: "com.diffplug.spotless"
	spotless {
		java {
			target "**/*.java"
			trimTrailingWhitespace()
			removeUnusedImports()
			googleJavaFormat()
			endWithNewline()
		}
		kotlin {
			target "**/*.kt"
			trimTrailingWhitespace()
			endWithNewline()
		}
	}
}

//version = "0.0.1-SNAPSHOT"
