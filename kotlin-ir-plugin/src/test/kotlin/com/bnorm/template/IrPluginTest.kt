/*
 * Copyright (C) 2020 Brian Norman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(ExperimentalCompilerApi::class)

package com.bnorm.template

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import kotlin.test.assertEquals
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test
import java.io.File

class IrPluginTest {

  @Test
  fun `IR plugin success for all files in directory`() {
    val sourceFiles = loadKotlinFilesFromDirectory("")
    val result = compile(sourceFiles)
    assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
  }
}

/**
 * Reads all Kotlin files from a directory and returns a list of `SourceFile` objects.
 */
fun loadKotlinFilesFromDirectory(directoryPath: String): List<SourceFile> {
  val dir = File(directoryPath)
  if (!dir.exists() || !dir.isDirectory) {
    throw IllegalArgumentException("Directory $directoryPath does not exist or is not a directory")
  }

  return dir.walkTopDown()  // Recursively iterate through all files
    .filter { it.isFile && it.extension == "kt" } // Only Kotlin files
    .map { file -> SourceFile.kotlin(file.name, file.readText()) }
    .toList()
}

fun compile(
  sourceFiles: List<SourceFile>,
  plugin: CompilerPluginRegistrar = TemplateCompilerRegistrar(),
): JvmCompilationResult {
  return KotlinCompilation().apply {
    sources = sourceFiles
    compilerPluginRegistrars = listOf(plugin)
    inheritClassPath = true
  }.compile()
}

fun compile(
  sourceFile: SourceFile,
  plugin: CompilerPluginRegistrar = TemplateCompilerRegistrar(),
): JvmCompilationResult {
  return compile(listOf(sourceFile), plugin)
}
