package com.mycompany.hibernate;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="Equipo")
public class Equipo implements Serializable{
    
    @Id
    @Column(name="id")
    private int id;
    @Column(name="nome")
    private String nome;
    @Column(name="cidade")
    private String cidade;
    @Column(name="numeroSocios")
    private int numeroSocios;
    
    @OneToMany(mappedBy = "equipo",cascade=CascadeType.ALL)
    private Set<Xogador> xogadores;
    
    @OneToOne(cascade= CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Entrenador entrenador;
    
    public Equipo(){
    }
    
    public Equipo(int id,String nome, String cidade, int numeroSocios, Entrenador entrenador){
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
        this.numeroSocios = numeroSocios;
        this.entrenador = entrenador;
        this.xogadores = new HashSet<>();

    }
    
    public void addXogador(Xogador xogador){
        this.xogadores.add(xogador);
    }
    
}
