import ReactDOM from 'react-dom';
import { configure, shallow, mount } from 'enzyme'
import Adapter from '@wojtekmaj/enzyme-adapter-react-17'
import i18n from 'i18next'
import { reactI18nextModule } from 'react-i18next';
import translationEn from './../assets/languages/en.json'
import translationNo from './../assets/languages/no.json'
import App from './../App'
import Home from './../Home/Home'
import SaveModal from './../Calendar/SaveModal/SaveModal'

// Configuring i18n module
const resources = {
    en: {
      translation: translationEn
    },
    no: {
      translation: translationNo
    }
}

i18n
  .use(reactI18nextModule)
  .init({
    resources,
    fallbackLng: 'en',
    debug: false,
    keySeparator: '.',
    interpolation: {escapeValue: false}
  });  

configure({adapter: new Adapter()})

describe('Direct testing, i18n is configured correctly', () => {
    it('English is set as default', () => {
        // assert
        expect(i18n.t('nav.menu')).toBe("Menu")
    })

    it('Language change is effective',  () => {
        // arrange
        i18n.changeLanguage('no')

        // assert
        expect(i18n.language).toBe("no")
        expect(i18n.t('nav.menu')).toBe("Meny")
    })
})

describe('Indirect testing, asserting translations',  () => {
    // Focusing on tags that are quick to test and have 
    //values that are less likely to be changed
    let app, appInst
    beforeEach(() => {
        app = mount(<App />)
        app.setState({authenticated: {jwt: "", opServId: ""}})
        appInst = app.instance()
    })

    it('<Home /> header Norwegian', () => {
        // arrange
        appInst.handleLangChange('no')

        // act
        app.setProps({})

        // assert
        let wrapper = mount(<Home {...{rosters: []}} />)
        expect(wrapper.contains("Du er ikke på vakt")).toBe(true)
    });


    it('<Home /> header English', () => {
      // arrange
      appInst.handleLangChange('en')

      // act
      app.setProps({})

      // assert
      let wrapper = mount(<Home {...{rosters: []}} />)
      expect(wrapper.find('h2').text()).toEqual("You are not on a shift")
  });


    it('<SaveModal /> Norwegian', () => {
        // arrange
        appInst.handleLangChange('no')
        let wrapper = mount(<SaveModal {...{show: true}} />)

        // assert
        expect(wrapper.containsMatchingElement(<button>Lagre</button>)).toBeTruthy()
        expect(wrapper.containsMatchingElement(<button>Lukk</button>)).toBeTruthy()
      })


    it('<SaveModal /> English', () => {
      // arrange
      appInst.handleLangChange('en')
      let wrapper = mount(<SaveModal {...{show: true}} />)

      // assert
      expect(wrapper.containsMatchingElement(<button>Save</button>)).toBeTruthy()
      expect(wrapper.containsMatchingElement(<button>Close</button>)).toBeTruthy()
    })
})