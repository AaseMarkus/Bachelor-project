import React, { Component } from 'react'
import { Button } from 'react-bootstrap'
import i18n from './../Language/i18n'
import Spinner from 'react-spinkit'
import './Home.css'

export default class Home extends Component {
    constructor(props) {
        super(props)
    
        this.state = {
             ongoing: false,
             currentOngoing: null,
             loading: false
        }
        this.handleClick = this.handleClick.bind(this)
        this.formatTime = this.formatTime.bind(this)
    }


    componentDidMount() {
        this.checkOngoing()
        document.title = i18n.t('title.home')
    }


    componentDidUpdate(prevProps) {
        if (prevProps.rosters != this.props.rosters) {
            this.checkOngoing()
        }   
    }


    handleClick() {
        if(this.state.ongoing) {
            let rosterStart = new Date(this.state.currentOngoing.fldOpRStartDate)
            let rosterStartTime = new Date(this.state.currentOngoing.fldOpRStartTime)
            rosterStart.setHours(rosterStartTime.getHours())
            rosterStart.setMinutes(rosterStartTime.getMinutes())
            rosterStart.setSeconds(rosterStartTime.getSeconds())

            this.props.deleteRoster(rosterStart, true)
            this.setState({ongoing: false, currentOngoing: null})
        } else {
            this.setState({loading: true})
            let startTime = new Date()
            startTime.setHours(startTime.getHours() - 1)
            this.props.addRoster(startTime, startTime, true, true)
            this.checkOngoing()
            setTimeout(() => {
                this.setState({loading: false})},
                2000
            )
        }
    }

    checkOngoing() {
        this.props.rosters.forEach(roster => {
            if(roster.fldOpRLogOn === true && roster.fldOpRLogOut === false) {
                this.setState({ongoing: true, currentOngoing: roster})
            }
        })
    }

    formatTime(rosterEnd) {
        let hour = rosterEnd.getHours() > 9 ? rosterEnd.getHours() : `0${rosterEnd.getHours()}`
        let minute = rosterEnd.getMinutes() > 9 ? rosterEnd.getMinutes() : `0${rosterEnd.getMinutes()}`
        let time = hour + ":" + minute
        return i18n.t('home.on-shift-until') + " " + time
    }

    render() {
        const buttonText = this.state.ongoing ? `${i18n.t('home.end-shift')}` : `${i18n.t('home.start-shift')}`
        let informationText = ""
        if (this.state.currentOngoing != null) {
            if (this.state.currentOngoing.fldOpREndTime === null)
                informationText = `${i18n.t('home.on-shift-openended')}`
            else {
                let rosterEnd = new Date(this.state.currentOngoing.fldOpREndTime)
                informationText = `${this.formatTime(rosterEnd)}`
            }
        } else informationText = `${i18n.t('home.not-on-shift')}`

        const spinner = <Spinner name="three-bounce"/>
        const content = 
            <div>
                <h2>{informationText}</h2>
                <br />
                <Button className="warning" variant="secondary" onClick={this.handleClick}>{buttonText}</Button>
            </div>

        return (
            <div className="homeContainer" role="main">
                {this.state.loading ? spinner : content}
            </div>
        )
    }
}
