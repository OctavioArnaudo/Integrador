//@file:Suppress("UNUSED_EXPRESSION")
//@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	alias(libs.plugins.android.application)
	//id("com.android.application") version "8.5.2" apply false
	/*
	groovy
	id("androidx.benchmark")
	id("androidx.navigation.safeargs.kotlin")
	id("com.android.dynamic-feature")
	id("com.android.library") version "4.4.1" apply false
	id("com.google.firebase.crashlytics")
	id("com.google.gms.google-services") version "4.4.2" apply false
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
	*/
}
android {
	namespace = "com.example.app"
	compileSdk = 34

	defaultConfig {
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
}
android {
	//compileSdkVersion = 34

	defaultConfig {
		applicationId = "com.example.app"
		minSdk = 26
		//minSdkVersion = 28
		//targetSdkVersion = 33
		targetSdk = 34
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

		/*
		manifestPlaceholders += mapOf()
		//consumerProguardFiles "consumer-rules.pro"
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
		vectorDrawables.useSupportLibrary = true
		vectorDrawables {
			useSupportLibrary = true
		}
		 */
	}
	/*
	//buildToolsVersion build_tools_version
	buildTypes {
		release {
			isDebuggable = false
			isDefault = true
			isShrinkResources = true
			//isShrinkResources = false
			//signingConfig signingConfigs.debug
		}
		debug {
			// Since debuggable can't be modified by gradle for library modules,
			// it must be done in a manifest - see src/androidTest/AndroidManifest.xml
			isMinifyEnabled = true
			//isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"benchmark-proguard-rules.pro"
			)
		}
	}
	compileOptions {
		//coreLibraryDesugaringEnabled true

		//kotlinCompilerExtensionVersion = "1.4.3"

		//sourceCompatibility = 1.8
		sourceCompatibility = JavaVersion.VERSION_1_8
		//sourceCompatibility = JavaVersion.VERSION_17

		//targetCompatibility = 1.8
		targetCompatibility = JavaVersion.VERSION_1_8
		//targetCompatibility = JavaVersion.VERSION_17
	}
	buildFeatures {
		compose = true
		viewBinding = true
	}
	composeOptions {
		//kotlinCompilerExtensionVersion = 1.8.20
		//kotlinCompilerVersion = 1.8.20
	}
	//kotlinOptions {
		//jvmTarget = "1.8"
		//jvmTarget = JavaVersion.VERSION_1_8.toString()
		//jvmTarget = JavaVersion.VERSION_17.majorVersion
		//allWarningsAsErrors = true
		//freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
		//useIR = true
	//}
	lint {
		baseline = file("lint-baseline.xml")
	}
	ndkVersion = "25.2.9519653"
	//signingConfigs {
		//release {
			//storeFile file("release.keystore")
			//keyAlias "release_key"
			//keyPassword "password"
			//v1SigningEnabled true
			//v2SigningEnabled true
		//}
	//}
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
	//packagingOptions {
		// for JNA and JNA-platform
		//exclude "META-INF/AL2.0"
		//exclude "META-INF/LGPL2.1"
	//}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	 */
}

dependencies {

	/*
	constraints {
		implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0") {
			because("kotlin-stdlib-jdk7 is now a part of kotlin-stdlib")
		}
		implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0") {
			because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
		}
	}
	 */

	androidTestImplementation(libs.ext.junit)
	androidTestImplementation(libs.espresso.core)
	/*
	androidTestImplementation(libs.androidx.benchmark.junit4)
	androidTestImplementation(libs.androidx.ui.test.junit4)
	androidTestImplementation(libs.test.core.ktx)
	androidTestImplementation(libs.espresso.accessibility)
	androidTestImplementation(libs.espresso.core)
	androidTestImplementation(libs.junit)
	androidTestImplementation(libs.junit.ktx)
	androidTestImplementation(libs.test.rules)
	androidTestImplementation(libs.androidx.runner)
	androidTestImplementation(libs.truth)
	androidTestImplementation(libs.junit)
	androidTestImplementation(libs.androidx.test.ext.junit)
	androidTestImplementation(libs.com.google.truth")
	androidTestImplementation(libs.espresso.core")
	androidTestImplementation(libs.kotlinx.coroutines.test")
	androidTestImplementation(libs.test.core")
	androidTestImplementation(libs.test.core.ktx")
	androidTestImplementation(libs.test.espresso.core")
	androidTestImplementation(libs.test.ext.junit")
	androidTestImplementation(libs.test.ext.junit.ktx")
	androidTestImplementation(libs.test.ext.truth")
	androidTestImplementation(libs.test.rules")
	androidTestImplementation(libs.test.runner")
	androidTestImplementation(libs.test.uiautomator")
	androidTestImplementation(libs.androidx.compose.bom)
	 */

	/*
	annotationProcessor(libs.room.compiler)
	annotationProcessor(libs.compiler)
	 */

	/*
	api(libs.androidx.room.ktx)
	 */

	/*
	coreLibraryDesugaring(libs.tools.desugar.jdk.libs)
	 */

	// We need this for tests. It should be 'debugImplementation' to put the test manifest in the
	// app package.
	/*
	debugImplementation(libs.ui.test.manifest)
	debugImplementation(libs.androidx.compose.ui.ui.tooling)
	 */

	implementation(libs.appcompat)
	implementation(libs.material)
	implementation(libs.biometric)
	implementation(libs.androidx.biometric)
	implementation(libs.androidx.constraintlayout)
	implementation(libs.sweetalert.library)
	implementation(libs.jbcrypt)
	/*
	implementation(libs.activity)
	implementation(libs.activity.compose)
	implementation(libs.activity.ktx)
	implementation(libs.androidx.annotation)
	implementation(libs.appcompat)
	implementation(libs.cardview)
	implementation(libs.material3)
	implementation(libs.androidx.compose.material.material)
	implementation(libs.material.icons.extended)
	implementation(libs.ui)
	implementation(libs.ui.graphics)
	implementation(libs.androidx.compose.ui.ui.tooling)
	implementation(libs.ui.tooling.preview)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.fragment)
	implementation(libs.fragment.ktx)
	implementation(libs.androidx.hilt.navigation.compose)
	implementation(libs.hilt.navigation.fragment)
	implementation(libs.androidx.legacy.support.v4)
	implementation(libs.lifecycle.livedata.ktx)
	implementation(libs.lifecycle.runtime.ktx)
	implementation(libs.lifecycle.viewmodel.ktx)
	implementation(libs.androidx.multidex.multidex)
	implementation(libs.navigation.compose)
	implementation(libs.androidx.navigation.fragment)
	implementation(libs.navigation.fragment.ktx)
	implementation(libs.androidx.navigation.ui)
	implementation(libs.androidx.navigation.ui.ktx)
	implementation(libs.androidx.preference)
	implementation(libs.preference.ktx)
	implementation(libs.recyclerview)
	implementation(libs.recyclerview.selection)
	implementation(libs.room.runtime)
	implementation(libs.androidx.slidingpanelayout)
	implementation(libs.androidx.webkit)
	implementation(libs.androidx.work.runtime)
	implementation(libs.lottie)
	implementation(libs.car.ui.lib)
	implementation(libs.support.multidex)
	implementation(libs.volley)
	implementation(libs.auth0)
	implementation(libs.fresco)
	implementation(libs.firebase.ui.auth)
	implementation(libs.firebaseui.firebase.ui.firestore)
	implementation(libs.glide)
	implementation(libs.philjay.mpandroidchart)
	implementation(libs.google.accompanist.flowlayout)
	implementation(libs.google.accompanist.insets.ui)
	implementation(libs.exoplayer)
	implementation(libs.extension.mediasession)
	implementation(libs.play.services.auth)
	implementation(libs.play.services.location)
	implementation(libs.play.services.maps)
	implementation(libs.gson)
	implementation(libs.hilt.android)
	implementation(libs.firebase.analytics)
	implementation(libs.google.firebase.analytics.ktx)
	implementation(libs.google.firebase.auth)
	implementation(libs.firebase.auth)
	implementation(libs.firebase.crashlytics)
	implementation(libs.firebase.database)
	implementation(libs.firebase.database.ktx)
	implementation(libs.firebase.firestore)
	implementation(libs.firebase.inappmessaging)
	implementation(libs.firebase.messaging)
	implementation(libs.firebase.storage)
	implementation(libs.core)
	implementation(libs.sdp.android)
	implementation(libs.zxing.android.embedded)
	implementation(libs.android.times.square)
	implementation(libs.logging.interceptor)
	implementation(libs.picasso)
	implementation(libs.converter.gson)
	implementation(libs.retrofit)
	implementation(libs.circleimageview)
	implementation(fileTree(dir: "libs", include: ["*.jar"]))
	implementation(libs.androidx.activity.ktx")
	implementation(libs.androidx.core.ktx")
	implementation(libs.androidx.wear")
	implementation(libs.kotlinx.coroutines.android")
	implementation(libs.material")
	implementation(libs.byte.buddy)
	implementation(libs.apache.groovy)
	implementation(libs.kotlin.reflect)
	implementation(libs.kotlin.stdlib)
	implementation(libs.kotlin.stdlib.jdk8)
	implementation(libs.kotlinx.coroutines.android)
	implementation(libs.openai.api.java)
	implementation(libs.boot.spring.boot.starter)
	implementation(libs.boot.spring.boot.starter.web)
	implementation(libs.compose.bom)
	implementation(platform(libs.firebase.bom))
	implementation(platform(libs.kotlin.bom))
	implementation(project(":app"))
	implementation(project(":shared"))
	implementation(libs.appcompat)
	implementation(libs.material)
	 */

	/*
	kapt(libs.androidx.hilt.hilt.compiler)
	kapt(libs.room.compiler)
	kapt(libs.hilt.android.compiler)
	*/

	/*
	providedRuntime(libs.spring.boot.starter.tomcat)
	 */

	testImplementation(libs.junit)
	/*
	testImplementation(libs.core.testing)
	testimplementation(libs.room.testing)
	testImplementation(libs.test.core)
	testimplementation(libs.ext.junit)
	testImplementation(libs.google.truth)
	testimplementation(libs.mockk)
	testImplementation(libs.kotlin.test.junit5)
	testimplementation(libs.kotlinx.coroutines.test)
	testImplementation(libs.mockito.core)
	testimplementation(libs.robolectric.robolectric)
	testImplementation(libs.springframework.spring.boot.starter.test)
	 */

	/*
	testRuntimeOnly(libs.platform.junit.platform.launcher)
	 */

}

/*
configurations.implementation {
	exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
}

allprojects {
	repositories {
		google()
		mavenCentral()
		maven { // repo for TFLite snapshot
			//name "ossrh-snapshot"
			//url "https://oss.sonatype.org/content/repositories/snapshots"
		}
	}
	tasks.withType<JavaCompile> {
		options.compilerArgs.add("-Xlint:deprecation")
		options.compilerArgs.add("-Xlint:unchecked")
	}
}

buildscript {
	dependencies {
		classpath(libs.gradle)
		classpath(libs.spotless.plugin.gradle)
		classpath(libs.crashlytics)
		classpath(libs.googleServices)
		classpath(libs.downloadTask)
		classpath(libs.kotlin.gradle.plugin)
		// NOTE: Do not place your application dependencies here; they belong
		// in the individual module build.gradle files
	}
	// ext {
		// Top-level variables used for versioning
	// }
	repositories {
		google()
		mavenCentral()
	}
}

//group = "com.example"

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
	delete(rootProject)
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
*/

//subprojects {
	//apply plugin: "com.diffplug.spotless"
	//spotless {
		//java {
			//target "**/*.java"
			//trimTrailingWhitespace()
			//removeUnusedImports()
			//googleJavaFormat()
			//endWithNewline()
		//}
		//kotlin {
			//target "**/*.kt"
			//trimTrailingWhitespace()
			//endWithNewline()
		//}
	//}
//}

//version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}