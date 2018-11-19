package amu.zhcet.common.extra;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.reflections.Reflections;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.io.File;
import java.util.EnumSet;

@Slf4j
public class Hibernate5DDLExporter {

    private String dialect = "org.hibernate.dialect.MySQL5InnoDBDialect";
    private String[] entityPackages;

    public Hibernate5DDLExporter dialect(String dialect) {
        this.dialect = dialect;
        return this;
    }

    private Hibernate5DDLExporter entities(String... entityPackage) {
        this.entityPackages = entityPackage;
        return this;
    }

    private Hibernate5DDLExporter schemaExport(String fileName, String targetDirectory) throws Exception {
        if (entityPackages == null && entityPackages.length == 0) {
            System.out.println("Not packages selected");
            System.exit(0);
        }
        File exportFile = createExportFileAndMakeDirectory(fileName, targetDirectory);

        PhysicalNamingStrategy physicalNamingStrategy;

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySetting(AvailableSettings.DIALECT, dialect)
                .applySetting(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy")
                .build();

        MetadataImplementor metadata = (MetadataImplementor) mapAnnotatedClasses(serviceRegistry).buildMetadata();

        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setOutputFile(exportFile.getAbsolutePath());
        schemaExport.setDelimiter(";");
        schemaExport.setFormat(true);
        schemaExport.execute(EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.CREATE, metadata, serviceRegistry);
        ((StandardServiceRegistryImpl) serviceRegistry).destroy();

        System.out.println(exportFile.getAbsolutePath());

        return this;

    }

    private File createExportFileAndMakeDirectory(String fileName, String targetDirectory) {
        File exportFile;
        if (targetDirectory != null) {
            final File directory = new File(targetDirectory);
            boolean created = directory.mkdirs();
            if (!created) log.error("Couldn't create directories");
            exportFile = new File(directory, fileName);
        } else {
            exportFile = new File(fileName);
        }
        return exportFile;
    }

    private MetadataSources mapAnnotatedClasses(ServiceRegistry serviceRegistry) {
        MetadataSources sources = new MetadataSources(serviceRegistry);

        final Reflections reflections = new Reflections();
        for (final Class<?> mappedSuperClass : reflections.getTypesAnnotatedWith(MappedSuperclass.class)) {
            sources.addAnnotatedClass(mappedSuperClass);
            System.out.println("Mapped = " + mappedSuperClass.getName());
        }
        for (final Class<?> entityClasses : reflections.getTypesAnnotatedWith(Entity.class)) {
            sources.addAnnotatedClass(entityClasses);
            System.out.println("Mapped = " + entityClasses.getName());
        }
        return sources;
    }

    public static Hibernate5DDLExporter instance() {
        return new Hibernate5DDLExporter();
    }

    public void main(String[] args) throws Exception {
        Hibernate5DDLExporter.instance()
                .entities("data.model")
                .schemaExport("create.sql", "build");
    }
}