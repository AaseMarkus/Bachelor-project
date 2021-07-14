import React, {Component} from 'react'
import { Button } from 'react-bootstrap' 
import { Redirect, Switch, Route, Link } from "react-router-dom"
import Spinner from 'react-spinkit'
import i18n from './../Language/i18n'
import CalendarWeek from './CalendarWeek/CalendarWeek'
import SaveIcon from './../Icons/SaveIcon'
import SaveModal from './SaveModal/SaveModal'
import './Calendar.css'


export default class Calendar extends Component {
    constructor(props) {
        super(props)
        this.state = {
            serverTime: null,
            minDate: null,
            maxDate: null,
            weekAmount: 4,
            showSaveModal: false,
            saves: 0
        }
        this.toggleSaveModal = this.toggleSaveModal.bind(this)
    }

    componentDidMount() {
        const url = this.props.baseUrl+'servertime'
        const options = {
            mode: 'cors',
            headers: {'Access-Control-Allow-Origin':'*'}
        }
        fetch(url, options)
            .then(async response => {
                //const data = await response.text()
                let data = "2021-05-20 18:10:16"
                if(!response.ok) {
                    const error = (data && data.message) || response.status
                    return Promise.reject(error)
                }

                let minMaxDates = this.calcMinMaxDates(data, this.props.minHour,
                    this.props.maxHour, this.state.weekAmount)
                this.setState({serverTime: new Date(data),
                               minDate: minMaxDates[0],
                               maxDate: minMaxDates[1]})
            })
            .catch(error => {
                console.log(error)
            }) 

        document.title = i18n.t('title.calendar')
    }

    // Helper method used to determine which week will be the first displayed in the calendar
    // The roll should occur when last week's max time is passed
    calcMinMaxDates(serverTime, minHour, maxHour, weekAmount) {
        let maxDate = new Date(serverTime)
        serverTime = new Date(serverTime)

        // Setting max date to the first sunday
        const weekdayAdjustment = maxDate.getDay() == 0 ? 0 : 7

        maxDate.setDate(maxDate.getDate() + weekdayAdjustment - maxDate.getDay())
        maxDate.setHours(maxHour)
        maxDate.setMinutes(0)
        maxDate.setSeconds(0)

        // In case workday spans past midnight
        const pastMidnight = maxHour > 0 && maxHour <= minHour
        if (pastMidnight) maxDate.setDate(maxDate.getDate() + 1)

        // Creating min date (monday) by cloning max date and subtracting 1 week
        let minDate = new Date(maxDate)
        minDate.setDate(minDate.getDate() - 7)

        while (minDate.getHours() != minHour)
            minDate.setHours(minDate.getHours() + 1) 
        
        // Adjusting min/max for time scenarios
        let maxDatePrev = new Date(maxDate)
        maxDatePrev.setDate(maxDatePrev.getDate() - 7)
        if (serverTime < maxDatePrev) {
            minDate.setDate(minDate.getDate() - 7)
            maxDate.setDate(maxDate.getDate() - 7)
        } else if (serverTime > maxDate) {
            minDate.setDate(minDate.getDate() + 7)
            maxDate.setDate(maxDate.getDate() + 7)
        }
        // Finally, adding extra weeks after the first
        maxDate.setDate(maxDate.getDate() + 7 * (weekAmount-1))

        return [minDate, maxDate]
    }

    componentWillUnmount() {
        // fix Warning: Can't perform a React state update on an unmounted component
        this.setState = (state, callback) => {
            return;
        };
    }

    toggleSaveModal() {
        let show = this.state.showSaveModal
        this.setState({showSaveModal: !show})
        // Resetting SaveModal
        if (!show) this.setState({saves: this.state.saves+1})
    }

    render() { 
        let rostersInWeeks = []

        if(this.state.serverTime != null && this.state.minDate != null 
            && this.state.maxDate != null) {
            for(let i = 0; i < this.state.weekAmount; i++) {
                let weeklyRoster = []
                let min = new Date(this.state.minDate)
                let max = new Date(this.state.maxDate)
                min.setDate(min.getDate() + (7 * i))
                max.setDate(max.getDate() - (7 * (this.state.weekAmount - 1 - i)))

                // Add dailyplans
                for(let j = 0; j < 7; j++) {
                    let dayMin = new Date(min)
                    let dayMax = new Date(max)
                    dayMin.setDate(dayMin.getDate() + j)
                    dayMax.setDate(dayMax.getDate() - 6 + j)

                    weeklyRoster[j] = this.props.rosters.filter(roster => {
                        let start = new Date(roster.fldOpRStartDate)
                        let time = new Date(roster.fldOpRStartTime)
                        start.setHours(time.getHours())
                        start.setMinutes(time.getMinutes())
                        start.setSeconds(time.getSeconds())
                        
                        return start.getTime() >= dayMin.getTime() && 
                                start.getTime() <= dayMax.getTime()
                    })
                }

                rostersInWeeks[i] = <CalendarWeek 
                    key={i} 
                    deleteRostersBetweenDates={this.props.deleteRostersBetweenDates}
                    serverTime={this.state.serverTime}
                    minDate={this.state.minDate}
                    cssTheme={this.props.cssTheme}
                    replaceRostersOnWeek={this.props.replaceRostersOnWeek}
                    replaceRostersOnDate={this.props.replaceRostersOnDate}
                    weekAmount={this.state.weekAmount} 
                    addRoster={this.props.addRoster} 
                    saveRosters={this.props.saveRosters}
                    deleteRoster={this.props.deleteRoster} 
                    toggleSaveModal={this.toggleSaveModal}
                    weeklyRoster={weeklyRoster}
                    startDate={min} 
                    endDate={max} />
            }
        }

        const saveModal = (
            <SaveModal
                key={this.state.saves}
                show={this.state.showSaveModal}
                toggleModal={this.toggleSaveModal}
                saveRosters={this.props.saveRosters}
                getRosters={this.props.getRosters}
                cssTheme={this.props.cssTheme}
            />
        )

        const saveButton = (
            <Button
                variant="success"
                onClick={this.toggleSaveModal}
                id="saveButton">
                    <SaveIcon/>{i18n.t('buttons.save')}
            </Button>
        )

        return (
            <div id="calendar" role="main">
                {this.state.serverTime === null ?
                    <div className="loader"><Spinner name="three-bounce"/></div> :
                    <div className="weekWrapper">
                        {rostersInWeeks}
                        {saveButton}
                        {saveModal}
                    </div>
                }
            </div>
        )
    }
}