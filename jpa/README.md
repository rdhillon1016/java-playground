# Resources

- [Quick Overview](https://jakarta.ee/learn/specification-guides/persistence-explained/)
- [Specification](https://jakarta.ee/specifications/persistence/3.2/)

# General

Tables are mapped to Java classes that contain special Jakarta Persistence annotations. Columns map to fields. These are called **entity** classes. Entities must override `equals()`, `hashCode()`, and `toString()`. Here's an example:

```java
@Entity
@Table(name = "PET_OWNER")
public class PetOwner implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @OneToMany(mappedBy = "petOwner")
    private Collection<Pet> petCollection;

    public PetOwner() {
    }

    public PetOwner(Integer id) {
        this.id = id;
    }

    // Accessor methods removed for brevity…

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PetOwner)) {
            return false;
        }
        PetOwner other = (PetOwner) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.eclipse.jakartapets.entity.PetOwner[ id=" + id + " ]";
    }   
}
```

In the previous example, the `mappedBy` indicates the column in the database table corresponding to the entity `Pet` that stores the key for the row in the `PET_OWNER` table. The field `petCollection` doesn't actually map to any attribute in the `PET_OWNER` table. Thus, the `Pet` entity must have:
```java
@JoinColumn(name = "PET_OWNER", referencedColumnName = "OWNER_ID")
@ManyToOne
private PetOwner petOwner;
```

It is important to note that one may specify a fetch type with the entity relationship annotations, which dictates how the data from related tables is retrieved. Fetch type options are EAGER and LAZY. The default `FetchType` for each is as follows:

* `@ManyToMany`: `FetchType.LAZY`
* `@ManyToOne`: `FetchType.EAGER`
* `@OneToMany`: `FetchType.LAZY`
* `@OneToOne`: `FetchType.EAGER`

Any database table that maps to an entity class is required to have a primary key. If you have a composite primary key, create a class for it and specify its usage in the entity class by annotating the entity class with `@IdClass(YourCompositeKey.class)`. **Generators** are a means for allowing entity classes to automatically generate a primary key field value.

An entity mananger is responsible for executing queries and returning data.

Jakarta Persistence specifies the Metamodel API that can be used to obtain metadata regarding entities that are managed by a persistence unit.

Creating a record is as easy as instantiating a new entity class, populating it with data, and persisting it by calling upon the entity manager `create()` method and passing the new object.

Jakarta Persistence offers a few different ways to construct queries, those being: Jakarta Persistence Query Language, Criteria API, and Named Queries. Using Jakarta Persistence Query Language, one can develop SQL-like queries to obtain data from the database through the use of entity classes. The Criteria Query API enables developers to use a CriteriaBuilder to construct queries, compound selections, expressions, predicates, and orderings. Lastly, Named Queries allow one to associate a static Jakarta Persistence Query Language query (or a native SQL query) to a name, and then call upon those names using an entity manager to execute.

Oftentimes, queries need to return distinct fields, rather than entire objects. When the fields being returned from a query do not correspond to the object relational mapping metadata, a `SqlResultSetMapping` must be used. This solution enables explicit field mapping to be provided to enable the persistence provider to map the results correctly. A brief example would be if one wishes to create a native query to obtain all pet names along with the pet type. This query will involve a join and it will return fields that are part of more than one entity:

```java
Query qry = em.createNativeQuery("select p.name, t.type " +
    "from PET p, PET_TYPE t " +
    "where t.id = p.type_id");

@SqlResultSetMapping(name="PetTypeResults", entities={
      @EntityResult(entityClass=org.eclipse.entity.Pet.class, fields={
          @FieldResult(name="name", column="name")
         }),
      @EntityResult(entityClass=org.eclipse.entity.PetType.class, fields={
          @FieldResult(name="petType", column="pet_type")
         })
  })
```

Jakarta Persistence provides the capability to generate database schema objects when an application is deployed.

A stored procedure is a code construct that is stored within a relational database. When invoked, the stored procedure will execute and can optionally return an outcome. Jakarta Persistence provides the `StoredProcedureQuery` interface to enable the invocation of database stored procedures. The `StoredProcedureQuery` interface is an extension of the `Query` interface. 