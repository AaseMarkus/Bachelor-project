import React from 'react'
import ReactDOM from 'react-dom';
import { configure, shallow, mount } from 'enzyme'
import Adapter from '@wojtekmaj/enzyme-adapter-react-17'
import App from './App'
import Login from './Login/Login'
import Toolbar from './Toolbar/Toolbar'

configure({adapter: new Adapter()})


describe('<App /> mounted, various',  () => {
  let app
  beforeEach(() => {
    app = mount(<App />)
  })


  it('Component renders without crashing', () => {
    const div = document.createElement('div');
    ReactDOM.render(<App />, div);
  });  


  it('Service change call is successful', () => {
    // act
    let appInst = app.instance()
    const services = {
      "Data Svar": {
        name: "Data Svar",
        fldServCssTheme: "data-svar",
        fldServMinHour: 6,
			  fldServMaxHour: 0
      }
    }

    // act
    app.setState({authenticated: {jwt: "", opServId: ""},
                 services: services}) 
    appInst.handleServiceChange("Data Svar")
    app.setProps({})

    // assert
    expect(app.childAt(0).hasClass("data-svar")).toBeTruthy()
  })


  it('Login renders if auth is null or undefined', () => {
    // assert
    expect(app.state('authenticated')).toBeFalsy()
    expect(app.find(Toolbar)).toHaveLength(0)
    expect(app.find(Login)).toHaveLength(1)
  })


  it('Login does get mounted when auth is defined as a dict', () => {
    // arrange
    app.setState({authenticated: {jwt: "", opServId: ""}})
    app.setProps({})

    //assert
    expect(app.state('authenticated')).toBeTruthy()
    expect(app.find(Toolbar)).toHaveLength(1)
    expect(app.find(Login)).toHaveLength(0)
  })
})


describe('<App /> shallow, various',  () => {
  let app, instance
  const opServId = "invalid opServId"
  const roster1 = {
    start: new Date(2021, 0, 1, 10, 5, 0),
    end: new Date(2021, 0, 1, 11, 15, 10),
    openEnded: false,
    shouldSave: false
  }
  const roster2 = {
    start: new Date(2021, 0, 1, 11, 32, 20),
    end: new Date(2021, 0, 1, 14, 55, 10),
    openEnded: false,
    shouldSave: false
  }
  const roster3 = {
    start: new Date(2021, 0, 1, 9, 30, 20),
    end: new Date(2021, 0, 3, 10, 15, 10),
    openEnded: false,
    shouldSave: false
  }
  

  beforeEach(() => {
    app = shallow(<App />)
    app.setState({authenticated: {jwt: "", opServId: opServId}})
    instance = app.instance()
  })


  it('Roster is added to list when calling addRoster-method', () => {
    // arrange

    // act
    instance.addRoster(roster1.start, roster1.end, roster1.openEnded, roster1.shouldSave);

    // assert
    // Confirming that roster is added
    expect(app.state('rosters')).toHaveLength(1)
    // Controlling datetimes
    const roster = app.state('rosters')[0]
    expect(roster.fldOpRStartDate).toBe("2021-01-01T00:00:00")
    expect(roster.fldOpRStartTime).toBe("1900-01-01T10:05:00")
    expect(roster.fldOpREndDate).toBe("2021-01-01T00:00:00")
    expect(roster.fldOpREndTime).toBe("1900-01-01T11:15:10")
    expect(roster.fldOpServID).toEqual(opServId)
  })


  it('Roster is deleted from list when calling deleteRoster-method', () => {
    // arrange

    // act
    instance.addRoster(roster1.start, roster1.end, roster1.openEnded, roster1.shouldSave);
    instance.addRoster(roster2.start, roster2.end, roster2.openEnded, roster2.shouldSave);
    instance.deleteRoster("2021-01-01 10:05:00", false)

    // assert
    // Confirming that roster is no longer in list
    expect(app.state('rosters')).toHaveLength(1)
    // Controlling that the correct roster was deleted
    const roster = app.state('rosters')[0]
    expect(roster.fldOpRStartDate).toBe("2021-01-01T00:00:00")
    expect(roster.fldOpRStartTime).toBe("1900-01-01T11:32:20")
    expect(roster.fldOpREndDate).toBe("2021-01-01T00:00:00")
    expect(roster.fldOpREndTime).toBe("1900-01-01T14:55:10")
  })


  it('Rosters for full day are deleted when calling deleteRostersBetweenDates', () => {
    // arrange

    // act
    instance.addRoster(roster1.start, roster1.end, roster1.openEnded, roster1.shouldSave);
    instance.addRoster(roster2.start, roster2.end, roster2.openEnded, roster2.shouldSave);
    instance.deleteRostersBetweenDates("2021-01-01 09:00:00", "2021-01-02 01:00:00")

    // assert
    // Confirming that roster is no longer in list
    expect(app.state('rosters')).toHaveLength(0)
  })


  it('Rosters for full week are deleted when calling deleteRostersBetweenDates', () => {
    // arrange

    // act
    instance.addRoster(roster1.start, roster1.end, roster1.openEnded, roster1.shouldSave);
    instance.addRoster(roster3.start, roster3.end, roster3.openEnded, roster3.shouldSave);
    instance.deleteRostersBetweenDates("2020-12-28 09:00:00", "2021-01-04 01:00:00")

    // assert
    // Confirming that roster is no longer in list
    expect(app.state('rosters')).toHaveLength(0)
  })


  it('Calling logOut-method results in successful deletion of related variables and redirect', () => {
    // arrange
    const app = mount(<App />)
    const instance = app.instance()
    app.setState({authenticated: {jwt: "", opServId: opServId}})

    // act
    instance.addRoster(roster1.start, roster1.end, roster1.openEnded, roster1.shouldSave)
    instance.logOut()
    app.setProps({})  // rerender necessary after state change

    // assert
    expect(app.state('authenticated')).toBeFalsy()
    expect(app.state('rosters')).toHaveLength(0)
    expect(app.find(Toolbar)).toHaveLength(0)
    expect(app.find(Login)).toHaveLength(1)
  })


  it('Rosters on single day are successfully copied when calling replaceRostersOnDate', () => {
    // arrange
    instance.addRoster(roster1.start, roster1.end, roster1.openEnded, roster1.shouldSave)
    instance.addRoster(roster2.start, roster2.end, roster2.openEnded, roster2.shouldSave)
    // roster that overlaps with both 1 on 2 wrt. start and end time is added to destination
    const roster3 = {  
        start: new Date(2021, 0, 3, 9, 30, 0),
        end: new Date(2021, 0, 3, 12, 15, 0),
        openEnded: false,
        shouldSave: false
    }
    instance.addRoster(roster3.start, roster3.end, roster3.openEnded, roster3.shouldSave)

    // act
    let rosters = app.state('rosters')
    instance.replaceRostersOnDate(new Date(2021, 0, 3, 9, 0, 0),
      rosters, new Date(2021, 0, 3, 9, 15, 0))
    rosters = app.state('rosters')  

    // assert
    expect(app.state('rosters')).toHaveLength(4)
    expect(searchInRosters(rosters, new Date(2021, 0, 3, 10, 5, 0), new Date(2021, 0, 3, 11, 15, 10))).toHaveLength(1)
    expect(searchInRosters(rosters, new Date(2021, 0, 3, 11, 32, 20), new Date(2021, 0, 3, 14, 55, 10))).toHaveLength(1)
  })


  it('Rosters for full week are successfully copied', () => {
    // arrange
    instance.addRoster(roster1.start, roster1.end, roster1.openEnded, roster1.shouldSave)
    instance.addRoster(roster2.start, roster2.end, roster2.openEnded, roster2.shouldSave)

    // act
    let rosters = app.state('rosters')
    instance.replaceRostersOnWeek(new Date(2021, 0, 4, 9, 0, 0),
      [[], [], [], [], rosters, [], []], new Date(2021, 0, 8, 9, 15, 0))
    rosters = app.state('rosters')

    // assert
    expect(app.state('rosters')).toHaveLength(4)
    expect(searchInRosters(rosters, new Date(2021, 0, 8, 10, 5, 0), new Date(2021, 0, 8, 11, 15, 10))).toHaveLength(1)
    expect(searchInRosters(rosters, new Date(2021, 0, 8, 11, 32, 20), new Date(2021, 0, 8, 14, 55, 10))).toHaveLength(1)
  })
})


// DRY helper method for finding single roster
function searchInRosters(list, searchStart, searchEnd) {
  let rosters = list.filter(r => {
    let startDate = new Date(r.fldOpRStartDate)
    let startTime = new Date(r.fldOpRStartTime)
    let endDate = new Date(r.fldOpREndDate)
    let endTime = new Date(r.fldOpREndTime)

    let startMatch = searchStart.getYear() === startDate.getYear() &&
      searchStart.getMonth() === startDate.getMonth() &&
      searchStart.getDate() === startDate.getDate() && 
      searchStart.getHours() === startTime.getHours() &&
      searchStart.getMinutes() === startTime.getMinutes() &&
      searchStart.getSeconds() === startTime.getSeconds()
    if (!startMatch) return false

    let endMatch = searchEnd.getYear() === endDate.getYear() &&
      searchEnd.getMonth() === endDate.getMonth() &&
      searchEnd.getDate() === endDate.getDate() &&
      searchEnd.getHours() === endTime.getHours() &&
      searchEnd.getMinutes() === endTime.getMinutes() &&
      searchEnd.getSeconds() === endTime.getSeconds()
    if (!endMatch) return false
    return true
  })
  return rosters
}
