package com.example.find_my_edge.common.storage.exception;

public class ImageUploadFailedException extends RuntimeException {
    public ImageUploadFailedException(String message) {
        super(message);
    }

    public ImageUploadFailedException() {
        super("Image upload failed");
    }
}
