package nl.wdudokvanheel.neat.lunar.neural;


import nl.wdudokvanheel.neat.lunar.game.model.Lander;
import nl.wdudokvanheel.neat.lunar.game.model.Vector2d;
import nl.wdudokvanheel.neural.neat.CreatureInterface;
import nl.wdudokvanheel.neural.neat.Species;
import nl.wdudokvanheel.neural.neat.genome.Genome;
import nl.wdudokvanheel.neural.network.Network;

public class NeatLander extends Lander implements CreatureInterface<NeatLander> {
    public Network network;
    private Genome genome;
    private Species<NeatLander> species;
    private double fitness;
    public int stagnant = 0;
    public Vector2d lastPosition = new Vector2d();

    public NeatLander(Genome genome) {
        this.genome = genome;
        this.network = new Network(genome);
    }

    @Override
    public Genome getGenome() {
        return genome;
    }

    @Override
    public Species<NeatLander> getSpecies() {
        return species;
    }

    @Override
    public double getFitness() {
        return fitness;
    }

    @Override
    public void setSpecies(Species<NeatLander> species) {
        this.species = species;
    }

    @Override
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}
