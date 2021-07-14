import React from 'react';
import ReactDOM from 'react-dom';
import { configure, shallow, mount } from 'enzyme'
import moment from 'moment'
import Adapter from '@wojtekmaj/enzyme-adapter-react-17'
import App from './../../App'
import TimePopout from './TimePopout';

configure({adapter: new Adapter()})

describe('<TimePopout /> various',  () => {
    it('Component renders without crashing', () => {
        const div = document.createElement('div')
        ReactDOM.render(<TimePopout />, div)
      })
})

describe('Roster overlapping each other or with min / max',  () => {
    const roster1 = {
        start: new Date(2021, 0, 1, 10, 5, 0),
        end: new Date(2021, 0, 1, 11, 34, 0),
    }
    const roster2 = {
        start: new Date(2021, 0, 1, 8, 55, 0),
        end: new Date(2021, 0, 1, 10, 5, 0),
    }
    const roster3 = {
        start: new Date(2021, 0, 2, 0, 55, 0),
        end: new Date(2021, 0, 2, 1, 5, 0),
    }
    let app, appInst
    const opServId = "invalid opServId"
    let props
    beforeEach(() => {
        app = shallow(<App />)
        app.setState({authenticated: {jwt: "", opServId: opServId}})
        appInst = app.instance()
        props = {
            start: new Date(2021, 0, 1, 9, 0, 0),
            end: new Date(2021, 0, 2, 1, 0, 0)
        }
    })


    it('Trying to add roster that overlaps at end', () => {
        // arrange
        appInst.addRoster(roster1.start, roster1.end, false, false)
        const rosters = app.state('rosters')
        props['dailyPlans'] = rosters
        const tpo = shallow(<TimePopout {...props} />)
        tpo.setState({openEnded: false})
        const tpoInst = tpo.instance()
        const oldEnd = tpoInst.createDate(rosters[0].fldOpREndDate,rosters[0].fldOpREndTime)
        let start = moment(oldEnd)
        let end = moment(start)
        end.add(10, 'minutes')

         // act
        tpoInst.onTimeChange([start, end])

        // assert
        expect(tpo.state('valid')).toBe(false)
    })

    
    it('Trying to add roster that overlaps at beginning', () => {
        // arrange
        appInst.addRoster(roster1.start, roster1.end, false, false)
        const rosters = app.state('rosters')
        props['dailyPlans'] = rosters
        const tpo = shallow(<TimePopout {...props} />)
        tpo.setState({openEnded: false})
        const tpoInst = tpo.instance()
        const oldStart = tpoInst.createDate(rosters[0].fldOpRStartDate,rosters[0].fldOpRStartTime)
        let end = moment(oldStart)
        let start = moment(end)
        start.add(-10, 'minutes')

         // act
        tpoInst.onTimeChange([start, end])

        // assert
        expect(tpo.state('valid')).toBe(false)
    })


    it('Trying to add open-ended roster that starts before another roster has begun', () => {
        // arrange
        appInst.addRoster(roster1.start, roster1.end, false, false)
        const rosters = app.state('rosters')
        props['dailyPlans'] = rosters
        const tpo = shallow(<TimePopout {...props} />)
        tpo.setState({openEnded: true})
        const tpoInst = tpo.instance()
        let start = moment(roster2.start)

         // act
        tpoInst.onTimeChange(start)

        // assert
        expect(tpo.state('valid')).toBe(false)
    })


    it('Adding open-ended roster right after normal roster', () => {
        // arrange
        appInst.addRoster(roster1.start, roster1.end, false, false)
        const rosters = app.state('rosters')
        props['dailyPlans'] = rosters
        const tpo = shallow(<TimePopout {...props} />)
        tpo.setState({openEnded: true})
        const tpoInst = tpo.instance()
        const oldEnd = tpoInst.createDate(rosters[0].fldOpREndDate,rosters[0].fldOpREndTime)
        let start = moment(oldEnd)
        start.add(1, 'minutes')

         // act
        tpoInst.onTimeChange(start)

        // assert
        expect(tpo.state('valid')).toBe(true)
    })


    it('Adding roster after logged out open-ended roster', () => {
        // arrange
        appInst.addRoster(roster1.start, null, true, false)
        const rosters = app.state('rosters')
        rosters[0].fldOpRLogOn = true
        rosters[0].fldOpRLogOut = true
        props['dailyPlans'] = rosters
        const tpo = shallow(<TimePopout {...props} />)
        tpo.setState({openEnded: false})
        const tpoInst = tpo.instance()
        const oldStart = tpoInst.createDate(rosters[0].fldOpRStartDate,rosters[0].fldOpRStartTime)
        let start = moment(oldStart)
        start.add(1, 'minutes')
        let end = moment(start)
        end.add(10, 'minutes')

         // act
        tpoInst.onTimeChange([start, end])

        // assert
        expect(tpo.state('valid')).toBe(true)
    })    


    it('Trying to add roster that starts earlier than allowed', () => {
        // arrange
        props['dailyPlans'] = []
        const tpo = shallow(<TimePopout {...props} />)
        tpo.setState({openEnded: false})
        const tpoInst = tpo.instance()
        let start = moment(roster2.start)
        let end = moment(roster2.end)

         // act
        tpoInst.onTimeChange([start, end])

        // assert
        expect(tpo.state('valid')).toBe(false)
    })


    it('Trying to add roster that should trigger end minute adjustment', () => {
        // It's possible to pick a time in the TimePicker that is after the 
        // allowed end time, for example 01:59 if max hour is 1. setStartEnd()
        // should detect and adjust this to 01:00. 
        // arrange
        props['dailyPlans'] = []
        const tpo = shallow(<TimePopout {...props} />)
        tpo.setState({openEnded: false})
        const tpoInst = tpo.instance()
        let start = moment(roster3.start)
        let end = moment(roster3.end)

         // act
        tpoInst.onTimeChange([start, end])

        // assert
        expect(tpo.state('valid')).toBe(true)
    })


    it('Trying to add roster that ends after allowed time (and too late to be adjusted)', () => {
        props['dailyPlans'] = []
        const tpo = shallow(<TimePopout {...props} />)
        tpo.setState({openEnded: false})
        const tpoInst = tpo.instance()
        let start = moment(roster3.start)
        let end = moment(new Date(2020, 0, 2, 2, 0, 0))

         // act
        tpoInst.onTimeChange([start, end])

        // assert
        expect(tpo.state('valid')).toBe(false)
    })    

})