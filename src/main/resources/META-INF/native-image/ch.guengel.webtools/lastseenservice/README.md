Should you encounter errors related to kotlin reflect similar to this when running the image

```
Exception in thread "main" java.lang.AssertionError: Built-in class kotlin.Any is not found
        at kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns$3.invoke(KotlinBuiltIns.java:93)
        at kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns$3.invoke(KotlinBuiltIns.java:88)
        at kotlin.reflect.jvm.internal.impl.storage.LockBasedStorageManager$MapBasedMemoizedFunction.invoke(LockBasedStorageManager.java:578)
        at kotlin.reflect.jvm.internal.impl.storage.LockBasedStorageManager$MapBasedMemoizedFunctionToNotNull.invoke(LockBasedStorageManager.java:651)
        at kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns.getBuiltInClassByName(KotlinBuiltIns.java:223)
        at kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns.getAny(KotlinBuiltIns.java:228)
        at kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns.getAnyType(KotlinBuiltIns.java:498)
        at kotlin.reflect.jvm.internal.impl.descriptors.NotFoundClasses$MockClassDescriptor.<init>(NotFoundClasses.kt:62)
        at kotlin.reflect.jvm.internal.impl.descriptors.NotFoundClasses$classes$1.invoke(NotFoundClasses.kt:45)
        at kotlin.reflect.jvm.internal.impl.descriptors.NotFoundClasses$classes$1.invoke(NotFoundClasses.kt:33)
        at kotlin.reflect.jvm.internal.impl.storage.LockBasedStorageManager$MapBasedMemoizedFunction.invoke(LockBasedStorageManager.java:578)
        at kotlin.reflect.jvm.internal.impl.storage.LockBasedStorageManager$MapBasedMemoizedFunctionToNotNull.invoke(LockBasedStorageManager.java:651)
        at kotlin.reflect.jvm.internal.impl.descriptors.NotFoundClasses.getClass(NotFoundClasses.kt:97)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.TypeDeserializer.typeConstructor$notFoundClass(TypeDeserializer.kt:136)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.TypeDeserializer.typeConstructor(TypeDeserializer.kt:141)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.TypeDeserializer.simpleType(TypeDeserializer.kt:79)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.TypeDeserializer.type(TypeDeserializer.kt:67)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.MemberDeserializer.valueParameters(MemberDeserializer.kt:332)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.MemberDeserializer.loadConstructor(MemberDeserializer.kt:268)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedClassDescriptor.computePrimaryConstructor(DeserializedClassDescriptor.kt:126)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedClassDescriptor.access$computePrimaryConstructor(DeserializedClassDescriptor.kt:33)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedClassDescriptor$primaryConstructor$1.invoke(DeserializedClassDescriptor.kt:64)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedClassDescriptor$primaryConstructor$1.invoke(DeserializedClassDescriptor.kt:64)
        at kotlin.reflect.jvm.internal.impl.storage.LockBasedStorageManager$LockBasedLazyValue.invoke(LockBasedStorageManager.java:408)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedClassDescriptor.getUnsubstitutedPrimaryConstructor(DeserializedClassDescriptor.kt:130)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedClassDescriptor.computeConstructors(DeserializedClassDescriptor.kt:133)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedClassDescriptor.access$computeConstructors(DeserializedClassDescriptor.kt:33)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedClassDescriptor$constructors$1.invoke(DeserializedClassDescriptor.kt:65)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedClassDescriptor$constructors$1.invoke(DeserializedClassDescriptor.kt:65)
        at kotlin.reflect.jvm.internal.impl.storage.LockBasedStorageManager$LockBasedLazyValue.invoke(LockBasedStorageManager.java:408)
        at kotlin.reflect.jvm.internal.impl.storage.LockBasedStorageManager$LockBasedNotNullLazyValue.invoke(LockBasedStorageManager.java:527)
        at kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedClassDescriptor.getConstructors(DeserializedClassDescriptor.kt:141)
        at kotlin.reflect.jvm.internal.KClassImpl.getConstructorDescriptors(KClassImpl.kt:203)
        at kotlin.reflect.jvm.internal.KClassImpl$Data$constructors$2.invoke(KClassImpl.kt:94)
        at kotlin.reflect.jvm.internal.KClassImpl$Data$constructors$2.invoke(KClassImpl.kt:93)
        at kotlin.reflect.jvm.internal.ReflectProperties$LazySoftVal.invoke(ReflectProperties.java:93)
        at kotlin.reflect.jvm.internal.ReflectProperties$Val.getValue(ReflectProperties.java:32)
        at kotlin.reflect.jvm.internal.KClassImpl$Data.getConstructors(KClassImpl.kt:93)
        at kotlin.reflect.jvm.internal.KClassImpl.getConstructors(KClassImpl.kt:238)
        at kotlin.reflect.full.KClasses.getPrimaryConstructor(KClasses.kt:36)
        at org.jetbrains.exposed.sql.Table.clone(Table.kt:1020)
        at org.jetbrains.exposed.sql.Table.cloneWithAutoInc(Table.kt:1037)
        at org.jetbrains.exposed.sql.Table.autoIncrement(Table.kt:625)
        at org.jetbrains.exposed.sql.Table.autoIncrement$default(Table.kt:624)
        at org.jetbrains.exposed.dao.id.IntIdTable.<init>(IdTable.kt:42)
        at org.jetbrains.exposed.dao.id.IntIdTable.<init>(IdTable.kt:41)
        at ch.guengel.webtools.dao.Seens.<init>(Seens.kt:9)
        at ch.guengel.webtools.dao.Seens.<clinit>(Seens.kt)
        at ch.guengel.webtools.services.LastSeenService.<clinit>(LastSeenService.kt:71)
        at ch.guengel.webtools.MainKt.main(Main.kt:37)
        at ch.guengel.webtools.MainKt.main(Main.kt)
```

then make sure you have at least

```json
[
  {
    "name": "kotlin.reflect.jvm.internal.ReflectionFactoryImpl",
    "allDeclaredConstructors": true
  },
  {
    "name": "kotlin.KotlinVersion",
    "allPublicMethods": true,
    "allDeclaredFields": true,
    "allDeclaredMethods": true,
    "allDeclaredConstructors": true
  },
  {
    "name": "kotlin.KotlinVersion[]"
  },
  {
    "name": "kotlin.KotlinVersion$Companion"
  },
  {
    "name": "kotlin.KotlinVersion$Companion[]"
  }
]
```

in your reflection configuration and

```json
{
  "resources": [
    {
      "pattern": "META-INF/.*.kotlin_module$"
    },
    {
      "pattern": "META-INF/services/.*"
    },
    {
      "pattern": ".*.kotlin_builtins"
    }
  ]
}
```

in your resource configuration.

Also, remember there is a trace agent that can generate configuration file for you, but it does not add the above items
to the configuration, though.

```
-agentpath:${GRAALVM_HOME}/lib/libnative-image-agent.so=config-output-dir=native-image-trace
```
