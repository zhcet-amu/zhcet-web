package in.ac.amu.zhcet.data.model.dto.mapper;

import in.ac.amu.zhcet.data.model.configuration.ConfigurationModel;
import in.ac.amu.zhcet.data.model.dto.Config;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConfigurationMapper {

    ConfigurationMapper MAPPER = Mappers.getMapper(ConfigurationMapper.class);

    @Mapping(source = "attendanceThreshold", target = "threshold")
    @Mapping(source = "url", target = "siteUrl")
    Config toConfig(ConfigurationModel configModel);

    @InheritInverseConfiguration
    ConfigurationModel fromConfig(Config config);

}
