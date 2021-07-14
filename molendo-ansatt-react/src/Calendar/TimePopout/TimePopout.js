import React, { Component } from 'react'
import { Button, Container, Row } from 'react-bootstrap'
import { TimePicker } from 'antd'
import i18n from './../../Language/i18n'
import moment from 'moment'
import 'antd/dist/antd.css'
import './TimePopout.css'

export default class TimePopout extends Component {
    constructor(props) {
        super(props)

        this.state = {
            btn: i18n.t('new.set-time'),
            valid: false,
            empty: true,
            openEnded: false
        }

        this.onTimeChange = this.onTimeChange.bind(this)
        this.isFormatValid = this.isFormatValid.bind(this)
        this.adjustForNextDay = this.adjustForNextDay.bind(this)
        this.isAvailable = this.isAvailable.bind(this)
        this.createDate = this.createDate.bind(this)
        this.setStartEnd = this.setStartEnd.bind(this)
        this.addRoster = this.addRoster.bind(this)
        this.setInvalid = this.setInvalid.bind(this)
        this.toggleOpenEnded = this.toggleOpenEnded.bind(this)
        this.getDisabledHours = this.getDisabledHours.bind(this)
    }

    getDisabledHours() {
        let hours = []
        let start = new Date(this.props.start)
        let end = new Date(this.props.end)

        if(start.getHours() != end.getHours()) {
            start.setHours(start.getHours() - 1)

            while(start.getHours() != end.getHours()) {
                hours.push(start.getHours())
                start.setHours(start.getHours() - 1)
            }
        }
        return hours
    }

    toggleOpenEnded() {
        this.setState({openEnded: !this.state.openEnded,
                       empty: true, btn: i18n.t('new.set-time')})
    }

    // called by Time/RangePicker when time is set
    onTimeChange(moments) {
        let formatValid = this.isFormatValid(moments)
        if (formatValid) {
            if (!moments.length) moments = [moments]
            let timespanValid = this.setStartEnd(moments)
            if (timespanValid) 
                this.setState({valid: true, btn: i18n.t('new.confirm'),
                               empty: false})
            else this.setInvalid()
        } else {
            this.setInvalid()
        }
    }

    // called by onTimeChange, creates Dates from moments and saves to state if available
    setStartEnd(moments) {
        let start = new Date(this.props.start)
        let end = new Date(this.props.end)

        start.setHours(moments[0].hours())
        start.setMinutes(moments[0].minutes())

        if (!this.state.openEnded) {
            if (end.getHours() !== moments[1].hours())
                end.setMinutes(moments[1].minutes())
            end.setHours(moments[1].hours())
        }

        // Dates have to be adjusted if workday ends after midnight
        if (start < this.props.start) this.adjustForNextDay(start, 1)
        if (end > this.props.end) this.adjustForNextDay(end, -1)
        if (start >= end) return
        let available = this.isAvailable(start, end)
        if (!available) return
        this.setState({start: start, end: end})
        return true
    }

    // Primitive first check
    isFormatValid(moments) {
        if (!moments) return
        if (!moments.length) moments = [moments]
        for (let i = 0; i < moments.length; i++) {
            if (!moments[i].isValid()) return
        }
        return true
    }

    // Adding a day if time is after midnight
    adjustForNextDay(date, adjustment) {
        date.setTime( date.getTime() + adjustment * 86400000 )
    }

    // Is the chosen timespan available? called by setStartEnd
    isAvailable(newStart, newEnd) {
        let plans = this.props.dailyPlans
        for (let i = 0; i < plans.length; i++) {
            let old = plans[i]

            if(old.fldOpRLogOn === true && old.fldOpRLogOut === true) continue

            let oldStart = this.createDate(old.fldOpRStartDate, old.fldOpRStartTime).getTime()
            let oldEnd = old.fldOpREndTime ? 
                this.createDate(old.fldOpREndDate, old.fldOpREndTime).getTime() : null
            
            if (this.state.openEnded || !oldEnd) return true
            
            if (newStart <= oldEnd && oldStart <= newEnd) return
        } 
        return true
    }

    // Helper method for comparing datetimes
    createDate(date, time) {
        date = new Date(date)
        time = new Date(time)
        date.setHours(time.getHours())
        date.setMinutes(time.getMinutes())
        date.setSeconds(time.getSeconds())
        return date
    }

    addRoster() {        
        this.props.addRoster(this.state.start, this.state.end, this.state.openEnded)
        this.setState({start: new Date(this.props.start),
                        end: new Date(this.props.end),
                        valid: false, btn: i18n.t('new.set-time'), empty: true})
        this.props.toggleOverlay()
    }

    setInvalid() {
        this.setState({valid: false, btn: i18n.t('new.invalid'), empty: false})
    }

    render() {
        let format = "HH:mm"
        let defaultFormat = moment('09:00', format)
        let timePicker = <TimePicker 
            size="large"
            showNow
            onChange={this.onTimeChange.bind(this)}
            format={format}
            disabledHours={this.getDisabledHours} 
            hideDisabledOptions={false}
            defaultFormat={defaultFormat}
            minuteStep={5}
            placeholder={i18n.t('new.choose-start')}
            getPopupContainer={triggerNode => {return triggerNode.parentNode}}

        />
        let rangePicker = <TimePicker.RangePicker
            className="rangepicker"
            size="large"
            order={false}
            onChange={this.onTimeChange.bind(this)}
            disabledHours={this.getDisabledHours} 
            format={format}
            placeholder={[i18n.t('new.start'), i18n.t('new.end')]}
            hideDisabledOptions={false}
            defaultFormat={defaultFormat}
            minuteStep={5} 
            getPopupContainer={triggerNode => {return triggerNode.parentNode}}
        />

        let submit = <Button
            variant="secondary"
            className={this.state.valid || this.state.empty ? 
                "validTimespan" : "invalidTimespan"}
            onClick={this.addRoster}
            disabled={!this.state.valid}>
                {this.state.btn}
        </Button>
            
        return (
            <Container className="timePopoutContainer">
                <Row>
                    {!this.state.openEnded ? rangePicker : timePicker}
                </Row>
                <Row>
                    <Button
                        onClick={this.toggleOpenEnded}
                        variant="secondary"
                        className={this.state.openEnded ? "warning-hover" : "warning"}>
                            {i18n.t('new.open-ended')}
                    </Button>
                    {submit}
                </Row>
            </Container>
        )
    }
}
