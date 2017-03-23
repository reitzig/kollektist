package org.reitzig.kollektist

// TODO document
data class Task(val description: String, val project: Project, val labels: Set<Label>)
data class Label(val name: String)
data class Project(val name: String)