# Add project specific ProGuard rules here.

# Retrofit / OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit

# Kotlinx Serialization
# Keep @Serializable and @SerialName if they are used by reflection
-keepattributes *Annotation*, EnclosingMethod, InnerClasses
