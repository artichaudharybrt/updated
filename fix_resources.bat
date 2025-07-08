@echo off
echo Fixing resource issues...

echo 1. Removing problematic files...
del /f /q "app\src\main\res\raw\airplane.mtl" 2>nul
del /f /q "app\src\main\res\raw\airplane_crash.mtl" 2>nul
del /f /q "app\src\main\res\raw\airplane_crash.obj" 2>nul
del /f /q "app\src\main\res\raw\airplane_model.obj" 2>nul
del /f /q "app\src\main\res\raw\aviator_3d.mtl" 2>nul
del /f /q "app\src\main\res\raw\aviator_3d_crash.mtl" 2>nul
del /f /q "app\src\main\res\raw\aviator_3d_crash.obj" 2>nul
del /f /q "app\src\main\res\raw\aviator_3d_model.obj" 2>nul

echo 2. Removing 3D view Java files...
del /f /q "app\src\main\java\com\gamegards\play24\_Aviator\SimpleAviatorRenderer.java" 2>nul
del /f /q "app\src\main\java\com\gamegards\play24\_Aviator\SimpleAviator3DView.java" 2>nul

echo Done! Now try rebuilding the project.
