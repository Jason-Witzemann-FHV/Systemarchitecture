com.hackoeur.jglm contains the SOURCES of the JGLM Java OpenGL Mathematics Library https://github.com/jroyalty/jglm

It is no longer maintained but has all functionality necessary for this project. Why included as source: Only Version 1.0 is on Maven Central but we need 1.0.1 for some extra features, therefore simply adding a dependency would not have worked. Providing a pre-built JAR for the students would have been problematic because this would require them to use the same Java Version as I do. Solution: simply add resources.
In general: DON'T DO THIS. However, this is a good reason for doing it.

Also I added / cleaned up functionality:
- added Vec2
- Removed warnings due to deprecated Java API usage in JglmConfig and Precision