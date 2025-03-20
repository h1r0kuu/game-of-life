# Conway's Game of Life

A JavaFX implementation of Conway's Game of Life, created as a project for Advanced Programming course at WSB University.

## Description

Conway's Game of Life is a cellular automaton devised by mathematician John Conway. The game consists of a grid of cells that can be either alive or dead. The state of each cell in the next generation is determined by a set of rules based on the current state of the cell and its eight neighbors.

## Features

- Interactive grid-based simulation
- Customizable grid size
- Start/Stop simulation
- Step-by-step generation advancement
- Pattern loading and saving
- Modern JavaFX-based user interface
- Responsive design

## Prerequisites

- Java Development Kit (JDK) 20 or later
- Maven 3.6 or later

## Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/game-of-life.git
cd game-of-life
```

2. Build the project using Maven:
```bash
mvn clean package
```

## Running the Application

You can run the application in two ways:

1. Using Maven:
```bash
mvn javafx:run
```

2. Using the generated JAR file:
```bash
java -jar target/gameoflife-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Usage

1. Launch the application
2. Use the controls to:
   - Start/Stop the simulation
   - Step through generations manually
   - Click on cells to toggle their state
   - Load or save patterns
   - Adjust simulation speed

## Project Structure

```
src/main/java/com/h1r0kuu/gameoflife/
├── components/     # UI components
├── controllers/    # FXML controllers
├── enums/         # Enumeration types
├── handlers/      # Event handlers
├── manages/       # Manager classes
├── models/        # Data models
├── renderer/      # Rendering logic
├── service/       # Business logic
├── utils/         # Utility classes
├── GameOfLife.java
├── GamePreloader.java
└── Main.java
```

## Technologies Used

- Java 20
- JavaFX 19.0.2.1
- ControlsFX 11.1.2
- Maven
- JUnit 5.9.2
- Log4j 2.20.0

## Author

Robert (Student ID: 16810)

## References

- [LifeViewer](https://lazyslug.com/lifeviewer/) - For pattern inspiration and validation
- [Icons8](https://icons8.com/) - For UI icons

## Screenshots

### Main Application Window
![java_AM3XZDF469](https://github.com/user-attachments/assets/36c07251-8812-45f1-8a63-0eb53b996bfd)


## License

This project is created as part of a university course assignment.
