# Use the official image as a parent image.
FROM openjdk:15

# Set the working directory.
WORKDIR /usr/btreebackend

# Copy the file from your host to your current location.
COPY btreebackend-0.0.1-SNAPSHOT.jar .

# Add metadata to the image to describe which port the container is listening on at runtime.
EXPOSE 8080

CMD ["java", "-jar", "btreebackend-0.0.1-SNAPSHOT.jar"]