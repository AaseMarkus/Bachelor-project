import React, { Component } from 'react'
import './DailyPopoutDay.css'

export default class DailyPopoutDay extends Component {
    constructor(props) {
        super(props)
    
        this.state = {
             checked: false,
             active: true,
        }

        this.changeChecked = this.changeChecked.bind(this)
        this.deactivate = this.deactivate.bind(this)
    }

    componentDidMount() {
        let currentDate = new Date(this.props.minDate)
        currentDate.setDate(currentDate.getDate() + this.props.dayNumber + 1)
        if(currentDate.getTime() < this.props.serverTime) this.deactivate()
    }

    changeChecked() {
        this.setState(prevState => {
            return {
                checked: !prevState.checked
            }
        }, () => {
            this.props.changeCopiedDay(this.state.checked, this.props.dayNumber)
        })
    }

    deactivate() {
        this.setState({ active: false })   
    }

    render() {
        return (
            <div
                className={`daycontainer ${this.state.active ? 'daycontaineractive' : ""}`}
                onClick={this.state.active ? this.changeChecked : undefined}>
                    {this.state.active &&
                     <input className="day-inp-cbx"
                        id={this.props.dayNumber}
                        type="checkbox"
                        readOnly
                        checked={this.state.checked}
                     />
                    }
                <br />
                {this.props.weekday}
            </div>
        )
    }
}
