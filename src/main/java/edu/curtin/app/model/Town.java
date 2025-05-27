package edu.curtin.app.model;

import edu.curtin.app.observer.Observer;
import edu.curtin.app.observer.Subject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Town implements Subject<Town> {
    private final String name;
    private int population;
    private int goodsStockpile;
    private int goodsTransportedToday;
    private final Set<Railway> railways = new HashSet<>();
    private final List<Observer<Town>> observers = new ArrayList<>();

    public Town(String name, int population) {
        this.name = name;
        this.population = population;
        this.goodsStockpile = 0;
        this.goodsTransportedToday = 0;
    }

    public String getName() { return name; }
    public int getPopulation() { return population; }
    public void setPopulation(int population) {
        this.population = population;
        notifyObservers();
    }
    public void addRailway(Railway railway) { railways.add(railway); }
    public Set<Railway> getRailways() { return railways; }
    public int getGoodsStockpile() { return goodsStockpile; }
    public void addGoods(int amount) { goodsStockpile += amount; }
    public void removeGoods(int amount) { goodsStockpile = Math.max(0, goodsStockpile - amount); }
    public int getGoodsTransportedToday() { return goodsTransportedToday; }
    public void resetGoodsTransportedToday() { goodsTransportedToday = 0; }
    public void addGoodsTransportedToday(int amount) { goodsTransportedToday += amount; }


    @Override
    public void addObserver(Observer<Town> o) { observers.add(o); }
    @Override
    public void removeObserver(Observer<Town> o) { observers.remove(o); }
    @Override
    public void notifyObservers() {
        for (Observer<Town> o : observers) o.update(this);
    }
}
