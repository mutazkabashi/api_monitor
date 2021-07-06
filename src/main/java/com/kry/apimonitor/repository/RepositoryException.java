package com.kry.apimonitor.repository;

/**
 * Repository Excpetion Handler Class
 * @author Mutaz Hussein Kabashi
 * @version 1.0
 *
 */
public class RepositoryException  extends RuntimeException  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public RepositoryException(String errorMessage) {
        super(errorMessage);
    }

    public RepositoryException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}
