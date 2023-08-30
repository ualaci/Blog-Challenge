package com.blogchallenge.blogchallenge.exceptions;

import java.util.Date;

public record ErrorResponse (Date timestamp, String message, String details) {}
