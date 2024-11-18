# Use Ubuntu 20.04 as the base image
FROM ubuntu:20.04

# Set environment variables for non-interactive installation
ENV DEBIAN_FRONTEND=noninteractive

# Install OpenJDK
RUN apt-get update && \
  apt-get install -y openjdk-11-jdk && \
  apt-get clean && \
  rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Copy the entire project directory into the container
COPY ./repl .

# Compile all Java files in the src/repl and src/test directories
RUN javac -cp "lib/*" src/repl/*.java src/test/*.java

# Optionally, run the tests
# CMD ["java", "-cp", "src:lib/*", "org.junit.runner.JUnitCore", "test.BNFParserTest", "test.ReplTest", "test.DomolectReplTest"]

# Run the main function, setting the classpath to include lib jars
CMD ["java", "-cp", "src:lib/*", "repl.Repl", "-f", "src/repl/domolect2-0.bnf"]
