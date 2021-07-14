import React, { Component } from 'react'
import CrossIcon from './../../Icons/CrossIcon'
import CheckIcon from './../../Icons/CheckIcon'
import { Modal, Button } from 'react-bootstrap'
import i18n from './../../Language/i18n'
import './RosterTime.css'

export default class RosterTime extends Component {
    constructor(props) {
        super(props)

        this.state = {
            showModal: false
        }

        this.delete = this.delete.bind(this)
        this.handleClose = this.handleClose.bind(this)
    }
    zeroPadTime(date) {
        let hour = date.getHours() > 9 ? date.getHours() : `0${date.getHours()}`
        let minute = date.getMinutes() > 9 ? date.getMinutes() : `0${date.getMinutes()}`
        return `${hour}:${minute}`
    }

    delete(roster, start) {
        if(roster.fldOpRLogOn === true && roster.fldOpRLogOut === false)
            this.setState({showModal: true})
        else this.props.deleteRoster(start) 
    }

    handleClose() {
        this.setState({showModal: false})
    }

    handleConfirmation(start) {
        this.props.deleteRoster(start)
        this.handleClose()
    }

    render() {
        let roster = this.props.roster;
        let startTime = new Date(roster.fldOpRStartTime)
        let start = new Date(roster.fldOpRStartDate)
        start.setHours(startTime.getHours())
        start.setMinutes(startTime.getMinutes())
        start.setSeconds(startTime.getSeconds())
        start.setMilliseconds(startTime.getMilliseconds())
        startTime = this.zeroPadTime(startTime)

        let endTime = roster.fldOpREndTime
        if (!endTime) endTime = "<>"
        else endTime = this.zeroPadTime(new Date(endTime))

        let spanClassName = ""
        if(!roster.fldOpRID) spanClassName = "newTime"
        else if ( roster.fldOpRLogOn === true && roster.fldOpRLogOut === false)
            spanClassName = "onGoingTime"

        return (
            <div className="rosterTime">
                <span className={spanClassName}>
                    {startTime} - {endTime}
                </span>
                <button
                    title={i18n.t('buttons.delete')}
                    className="deleteButton"
                    disabled={this.props.locked}
                    onClick={() => this.delete(roster, start)}>
                    {(roster.fldOpRLogOn === true && roster.fldOpRLogOut === true) ? 
                        <CheckIcon fill="#83E22B" stroke="#83E22B" /> : <CrossIcon />}
                </button>

                <Modal
                    className={this.props.cssTheme}
                    show={this.state.showModal}
                    onHide={this.handleClose}
                    backdrop="static"
                    keyboard={false} 
                >
                    <Modal.Header closeButton>
                        <Modal.Title>{i18n.t('delete.title')}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        {i18n.t('delete.body')}
                    </Modal.Body>
                    <Modal.Footer>
                    <Button
                            className="confirm" 
                            variant="primary" 
                            onClick={() => this.handleConfirmation(start)}>
                            {i18n.t('delete.yes')}
                        </Button>
                        <Button
                            className="error"
                            variant="secondary"
                            onClick={this.handleClose}>
                            {i18n.t('delete.close')}
                        </Button>

                    </Modal.Footer>
                </Modal>
            </div>
        )
    }
}
