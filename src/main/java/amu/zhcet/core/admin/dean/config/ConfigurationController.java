package amu.zhcet.core.admin.dean.config;

import amu.zhcet.data.config.Configuration;
import amu.zhcet.data.config.ConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/dean/configuration")
public class ConfigurationController {

    private final ModelMapper modelMapper;
    private final ConfigurationService configurationService;

    @Autowired
    public ConfigurationController(ModelMapper modelMapper, ConfigurationService configurationService) {
        this.modelMapper = modelMapper;
        this.configurationService = configurationService;
    }

    private Config toConfig(Configuration configuration) {
        Config config = modelMapper.map(configuration, Config.class);
        config.setTerm(configuration.getSession().charAt(0));
        config.setYear(2000 + Integer.parseInt(configuration.getSession().substring(1)));

        return config;
    }

    private Configuration toConfigModel(Config config) {
        Configuration configuration = modelMapper.map(config, Configuration.class);
        configuration.setSession(config.getTerm() + String.valueOf(config.getYear() - 2000));

        return configuration;
    }

    @GetMapping
    public String configuration(Model model) {
        model.addAttribute("page_title", "Configuration");
        model.addAttribute("page_subtitle", "Configuration Management Panel");
        model.addAttribute("page_description", "Manage site wide configurations and parameters");

        if (!model.containsAttribute("config"))
            model.addAttribute("config", toConfig(configurationService.getConfigCache()));
        return "dean/configuration";
    }

    @PostMapping
    public String configurationPost(RedirectAttributes redirectAttributes, @Valid Config config, BindingResult result) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("config", config);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.config", result);
        } else {
            List<String> errors = new ArrayList<>();
            if (config.getTerm() != 'A' && config.getTerm() != 'W')
                errors.add("Term can only be Autumn or Winter");

            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("config", config);
                redirectAttributes.addFlashAttribute("errors", errors);
            } else {
                configurationService.save(toConfigModel(config));
                redirectAttributes.addFlashAttribute("success", Collections.singletonList("Configuration successfully saved!"));
            }
        }

        return "redirect:/admin/dean/configuration";
    }

}
