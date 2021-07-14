import i18n from 'i18next';
import { reactI18nextModule } from 'react-i18next';
import detector from 'i18next-browser-languagedetector'
import translationEn from './../assets/languages/en.json'
import translationNo from './../assets/languages/no.json'

const resources = {
  en: {
    translation: translationEn
  },
  no: {
    translation: translationNo
  }
}

i18n
  // load translation using http -> see /public/locales (i.e. https://github.com/i18next/react-i18next/tree/master/example/react/public/locales)
  // learn more: https://github.com/i18next/i18next-http-backend
  //.use(Backend)
  // detect user language
  // learn more: https://github.com/i18next/i18next-browser-languageDetector
  .use(detector)
  // pass the i18n instance to react-i18next.
  .use(reactI18nextModule)
  // init i18next
  // for all options read: https://www.i18next.com/overview/configuration-options
  .init({
    resources,
    fallbackLng: 'en',
    debug: false,
    keySeparator: '.',
    
    interpolation: {
      escapeValue: false, // not needed for react as it escapes by default
    }
  });


export default i18n