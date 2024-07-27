#-----------------------
# Compiler/linker flags
#-----------------------

Compiler = javac
Runner = java

# Compiler flags:
# CFLAGS = \
# 	-std=c++17 \
# 	-O2
Modules = javafx.controls
Path_to_javaFX = /home/kuraterut/Desktop/HSE/my_projects/JavaProjects/javafx-sdk-22.0.2/lib
Main_class = Main
Bin_dir = bin
Files = src/Main.java



#--------
# Colors
#--------

# Use ANSI color codes:
BRED    = \033[1;31m
BGREEN  = \033[1;32m
BYELLOW = \033[1;33m
GREEN   = \033[1;35m
BCYAN   = \033[1;36m
RESET   = \033[0m

#-------
# Files
#-------

# INCLUDES = $(wildcard include/*.hpp)

# Add "include" folder to header search path:
# CFLAGS += -I $(abspath include)

# List of sources:
# SOURCES = $(wildcard src/*.cpp)


# OBJECTS = $(SOURCES:src/%.cpp=build/%.o)

# EXECUTABLE = build/test

#---------------
# Build process
#---------------

# By default, build executable:
# NOTE: first target in the file is the default.



default: $(Main_class)

# Link all object files together to obtain a binary:
# NOTE: all object files will be built first.
$(Main_class): $(Files) Makefile
	@mkdir -p $(Bin_dir)
	$(Compiler) --module-path $(Path_to_javaFX) --add-modules=$(Modules) -d $(Bin_dir) $(Files)

run: $(Main_class) Makefile
	$(Runner) --module-path $(Path_to_javaFX) --add-modules=$(Modules) -classpath ./$(Bin_dir) $(Main_class)



clean:
	@printf "$(BYELLOW)Cleaning build directory $(RESET)\n"
	rm -rf $(Bin_dir)

# List of non-file targets:
.PHONY: run clean default
	

