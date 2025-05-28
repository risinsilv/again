package edu.curtin.app.observer;
/**
 * ============================================================
 * | Class Name  : Subject                                   |
 * | Author      : Risinu Silva                              |
 * | Date        : 11 April 2025                             |
 * | Description : Subject interface for the observer        |
 * |               pattern.                                  |
 * ============================================================
 */
public interface Subject<T> {
    void addObserver(Observer<T> o);
    void removeObserver(Observer<T> o);
    void notifyObservers();
}
