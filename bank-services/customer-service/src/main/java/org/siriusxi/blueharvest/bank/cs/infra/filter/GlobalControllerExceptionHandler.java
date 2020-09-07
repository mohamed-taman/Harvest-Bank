package org.siriusxi.blueharvest.bank.cs.infra.filter;

import lombok.extern.log4j.Log4j2;
import org.siriusxi.blueharvest.bank.common.exception.InvalidInputException;
import org.siriusxi.blueharvest.bank.common.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

/**
 * The class Global controller exception handler is a generic and central point for all
 * microservices that handles all services exceptions.
 *
 * <p>It act as filter so it is pluggable component just added to microservice context
 * automatically, when you add <code>ComponentScan</code> on your application.
 *
 * @see org.springframework.context.annotation.ComponentScan
 * @author Mohamed Taman
 * @version 0.5
 * @since Harvest beta v0.1
 */
@RestControllerAdvice
@Log4j2
class GlobalControllerExceptionHandler {

  /**
   * Method to handle <i>Not found exceptions</i> http error info.
   *
   * @param ex the ex to get its information
   * @return the http error information.
   * @since v0.1
   */
  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public @ResponseBody
  HttpErrorInfo handleNotFoundExceptions(Exception ex) {

    return createHttpErrorInfo(NOT_FOUND, ex);
  }

  /**
   * Method to handle <i>invalid input exception</i> http error info.
   *
   * @param ex the ex to get its information
   * @return the http error information.
   * @since v0.1
   */
  @ResponseStatus(UNPROCESSABLE_ENTITY)
  @ExceptionHandler(InvalidInputException.class)
  public @ResponseBody
  HttpErrorInfo handleInvalidInputException(Exception ex) {

    return createHttpErrorInfo(UNPROCESSABLE_ENTITY, ex);
  }

  private HttpErrorInfo createHttpErrorInfo(
      HttpStatus httpStatus , Exception ex) {
    final var message = ex.getMessage();

    log.debug("Returning HTTP status: {}, message: {}", httpStatus, message);
    return new HttpErrorInfo(httpStatus, message);
  }
}
