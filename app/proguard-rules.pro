# Add project specific ProGuard rules here.

# Retrofit / OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit

# Kotlinx Serialization
# Keep @Serializable and @SerialName if they are used by reflection
-keepattributes *Annotation*, EnclosingMethod, InnerClasses

# Jetpack Glance - prevent R8 class merging, renaming, and stripping
-keep public class * extends androidx.glance.appwidget.GlanceAppWidget {
    public <init>();
    *;
}
-keep public class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver {
    public <init>();
    *;
}

# WorkManager
-keep class me.amirkazemzade.netwidget.presentation.RemainedWidgetUpdateWorker {
    public <init>(...);
    *;
}

