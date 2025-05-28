package edu.curtin.app.observer;
/**
 * ============================================================
 * | Class Name  : Observer                                  |
 * | Author      : Risinu Silva                              |
 * | Date        : 11 April 2025                             |
 * | Description : Observer interface for the observer       |
 * |               pattern.                                  |
 * ============================================================
 */
public interface Observer<T> {
    void update(T arg);
}
