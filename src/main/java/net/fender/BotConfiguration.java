package net.fender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Configuration
public class BotConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(BotConfiguration.class);

//    @Bean
//    public Locale defaultLocale(@Value("${default-locale}") String defaultLocaleName) {
//        for (Locale locale : Locale.getAvailableLocales()) {
//            if (Objects.equals(defaultLocaleName, locale.toString())) {
//                LOG.info("setting default locale to {}", locale);
//                Locale.setDefault(locale);
//                return locale;
//            }
//        }
//        LOG.warn("locale {} not found, using default locale {}", defaultLocaleName, Locale.getDefault());
//        return Locale.getDefault();
//    }
}
