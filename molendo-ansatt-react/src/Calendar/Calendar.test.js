import React from 'react';
import ReactDOM from 'react-dom';
import { configure, shallow, mount } from 'enzyme'
import Adapter from '@wojtekmaj/enzyme-adapter-react-17'
import Calendar from './Calendar';

configure({adapter: new Adapter()})


describe('<Calendar /> various', () => {
  it('renders without crashing', () => {
    const div = document.createElement('div')
    ReactDOM.render(<Calendar />, div)
  })  
})

describe('<Calendar /> roll to next week is done correctly',  () => {
  const minHour = 6;
  const weekAmount = 4


  it('Workday ends after midnight (ie. on monday), server time is right before', () => {
    // arrange
    let maxHour = 1;
    let serverTime = "2021-01-11 00:59:59"
    let wrapper = shallow(<Calendar />)
    let instance = wrapper.instance()

    // act
    let minMax = instance.calcMinMaxDates(serverTime, minHour, maxHour, weekAmount)
    
    // assert
    expect(minMax[0]).toEqual(new Date("2021-01-04T06:00:00"))
    expect(minMax[1]).toEqual(new Date("2021-02-01T01:00:00"))
  })


  it('Workday ends after midnight (ie. on monday), server time is right after', () => {
    // arrange
    let maxHour = 1;
    let serverTime = "2021-01-11 01:01:01"
    let wrapper = shallow(<Calendar />)
    let instance = wrapper.instance()

    // act
    let minMax = instance.calcMinMaxDates(serverTime, minHour, maxHour, weekAmount)
    
    // assert
    expect(minMax[0]).toEqual(new Date("2021-01-11T06:00:00"))
    expect(minMax[1]).toEqual(new Date("2021-02-08T01:00:00"))
  })


  it('Workday ends after midnight (ie. on monday), server time is after opening', () => {
    // arrange
    let maxHour = 1;
    let serverTime = "2021-01-04 06:00:01"
    let wrapper = shallow(<Calendar />)
    let instance = wrapper.instance()

    // act
    let minMax = instance.calcMinMaxDates(serverTime, minHour, maxHour, weekAmount)
    
    // assert
    expect(minMax[0]).toEqual(new Date("2021-01-04T06:00:00"))
    expect(minMax[1]).toEqual(new Date("2021-02-01T01:00:00"))
  })


  it('Workday ends before midnight (ie. on sunday), server time is right before', () => {
    // arrange
    let maxHour = 23;
    let serverTime = "2021-01-10 22:59:59"
    let wrapper = shallow(<Calendar />)
    let instance = wrapper.instance()

    // act
    let minMax = instance.calcMinMaxDates(serverTime, minHour, maxHour, weekAmount)
    
    // assert
    expect(minMax[0]).toEqual(new Date("2021-01-04T06:00:00"))
    expect(minMax[1]).toEqual(new Date("2021-01-31T23:00:00"))
  })
  

  it('Workday ends before midnight (ie. on sunday), server time is right after', () => {
    // arrange
    let maxHour = 23;
    let serverTime = "2021-01-10 23:00:01"
    let wrapper = shallow(<Calendar />)
    let instance = wrapper.instance()

    // act
    let minMax = instance.calcMinMaxDates(serverTime, minHour, maxHour, weekAmount)
    
    // assert
    expect(minMax[0]).toEqual(new Date("2021-01-11T06:00:00"))
    expect(minMax[1]).toEqual(new Date("2021-02-07T23:00:00"))
  })


  it('Workday ends before midnight (ie. on sunday), server time is after opening', () => {
    // arrange
    let maxHour = 23;
    let serverTime = "2021-01-11 06:00:01"
    let wrapper = shallow(<Calendar />)
    let instance = wrapper.instance()

    // act
    let minMax = instance.calcMinMaxDates(serverTime, minHour, maxHour, weekAmount)
    
    // assert
    expect(minMax[0]).toEqual(new Date("2021-01-11T06:00:00"))
    expect(minMax[1]).toEqual(new Date("2021-02-07T23:00:00"))
  })
})