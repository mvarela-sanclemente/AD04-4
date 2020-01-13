
# Relación moitos a moitos
Vamos ver como creamos unha relación moitos a moitos.
No noso caso imos engadir engadir posicións. Cada xogador pode ter 0 ou n posicións, e para cada posición, pode haber 0 ou n xogadores.
Exemplo:
```java
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="Posicion")
public class Posicion implements Serializable{
    
    public static final int PORTEIRO=0;
    public static final int DEFENSA=1;
    public static final int CENTROCAMPISTA=2;
    public static final int DELANTERO=3;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    @Column(name="linea")
    private int linea;
    @Column(name="nomePosicion")
    private String nomePosicion;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name="PosicionXogador", joinColumns={@JoinColumn(name="IdPosicion")}, inverseJoinColumns={@JoinColumn(name="IdXogador")})
    private Set<Xogador> xogadores;   
    
    public Posicion(){}
    
    public Posicion(int linea,String nomePosicion){
        this.linea=linea;
        this.nomePosicion=nomePosicion;
        this.xogadores = new HashSet<>();
    }
    
    public void addXogadores(Xogador xogador){
        this.xogadores.add(xogador);
    }
    
}
```
Nesta calse o único que temos novo son as liñas:
- **ManyToMany**. Indicámoslle a Hibernate que estamos nunha relación n..n.
    - **cascade**: mismo significado que vimos nos outros casos.
- **JoinTable**: anotación para poder realizar os joins. 
    - **name**: é o nome da tábla da relación n..n.
    - **joinColumns**: contén o nome da columna da táboa creada para a relación que é utilizada como clave foránea con esta clase.
    - **inverseJoinColumns**: contén o nome da columna da táboa creada para a relación que é utilizada como clave foránea da outra clase, neste caso da clase xogador.

```java
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="Xogador")
public class Xogador implements Serializable{
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    @Column(name="nome")
    public String nome;
    @Column(name="dorsal")
    public int dorsal;
    @ManyToOne
    @JoinColumn(name="idEquipo")
    private Equipo equipo;
    
    @ManyToMany(cascade = {CascadeType.ALL},mappedBy="xogadores")
    private Set<Posicion> posicions;
    
    public Xogador(){}
    
    public Xogador(String nome,int dorsal,Equipo equipo){
        this.nome = nome;
        this.dorsal = dorsal;
        this.equipo = equipo;
        this.posicions = new HashSet<>();

    }
    
    public void addPosicion(Posicion posicion){
        this.posicions.add(posicion);
    }
    
}

```
Neta clase engadimos a clase Set para gardar todos os posicións do xogador. E para ese atributo definimos as seguintes anotacións:
- **OneToMany**: esta anotación indica que para este atributo estamos do lado un a moitos.
- **mappedBy**: e o nome do atributo da clase ca que se relaciona que contén o tipo de datos desta clase. Neste exemplo, é o nome do atributo da clase Posicion que garda os xogadores desa posición. É decir, o atributo **posicions**.

> Recordade engadir a persistencia da clase Posicion a clase HibernateUtil.

```java
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Main {
    
    public static void main(String args[]){
        //Creamos un entrenador
        Entrenador entrenador = new Entrenador(1,"Nome1","Apelidos1",50);
        
        //Creamos un equipo
        Equipo equipo = new Equipo(1,"San Clemente", "Santiago", 1000, entrenador);
        
        //Creamos as posicions
        Posicion porteiro = new Posicion(Posicion.PORTEIRO,"Porteiro");
        Posicion central = new Posicion(Posicion.DEFENSA,"Central");
        Posicion lateral = new Posicion(Posicion.DEFENSA,"Lateral");
        Posicion interior = new Posicion(Posicion.CENTROCAMPISTA,"Interior");
        
        //Creamos os xogadores
        Xogador xogador1 = new Xogador("nome1",1,equipo);
        xogador1.addPosicion(porteiro);
        porteiro.addXogadores(xogador1);
        equipo.addXogador(xogador1);
        
        Xogador xogador2 = new Xogador("nome2",2,equipo);
        xogador1.addPosicion(lateral);
        lateral.addXogadores(xogador2);
        xogador1.addPosicion(central);
        central.addXogadores(xogador2);
        equipo.addXogador(xogador2);
        
        Xogador xogador3 = new Xogador("nome3",3,equipo);
        xogador3.addPosicion(central);
        central.addXogadores(xogador3);
        xogador3.addPosicion(interior);
        interior.addXogadores(xogador3);
        equipo.addXogador(xogador3);
        
        Transaction tran = null;
        
        
        try{
            //Collemos a sesión de Hibernate
            Session session = HibernateUtil.getSessionFactory().openSession();
            //Comenzamos unha transacción
            tran = session.beginTransaction();
            
            //Gardamos o equipo
            session.save(equipo);
            
            //Facemos un commit da transacción
            tran.commit();
        }catch(HibernateException e){
            e.printStackTrace();
        }
    }
    
}
```
Aquí temos a clase Main para probar o que fixemos.

