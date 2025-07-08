@echo off
echo Cleaning up project...

echo 1. Removing build directories...
rmdir /s /q build
rmdir /s /q .gradle

echo 2. Removing problematic files...
del /f /q "src\main\res\raw\airplane.mtl" 2>nul
del /f /q "src\main\res\raw\airplane_crash.mtl" 2>nul
del /f /q "src\main\res\raw\airplane_crash.obj" 2>nul
del /f /q "src\main\res\raw\airplane_model.obj" 2>nul

echo Done! Now try rebuilding the project.
